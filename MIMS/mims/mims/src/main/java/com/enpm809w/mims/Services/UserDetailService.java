package com.enpm809w.mims.Services;

import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.Common.UserDetail;
import com.enpm809w.mims.PasswordValidator.InvalidPasswordException;
import com.enpm809w.mims.PasswordValidator.PasswordComplexity;
import com.enpm809w.mims.Repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.util.Date;

@Service
@Transactional
public class UserDetailService implements UserDetailsService{
    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public static final int MAX_FAILED_ATTEMPTS = 3;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        repo.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    public void resetFailedAttempts(String email) {
        repo.updateFailedAttempts(0, email);
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        repo.save(user);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            repo.save(user);

            return true;
        }

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserDetail(user);
    }

    public boolean changeUserPassword(String userName, String oldPassword, String newPassword){
        try {

            User user = repo.findByEmail(userName);
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return false;

            } else {
                PasswordComplexity.isValid(newPassword);
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);
                repo.save(user);
                return true;
            }
        } catch (InvalidPasswordException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean register(User user, String url, UserRepository userRepo) {
        try {
//            if (userRepo.existsById(user.getId())) {
//                throw new Exception("invalid");
//            }
            PasswordComplexity.isValid(user.getPassword());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            String randomCode = RandomString.make(64);
            user.setVerificationCode(randomCode);
            user.setEnabled(false);
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            userRepo.save(user);
            if (sendVerificationEmail(user, url))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        try {
            String toAddress = toIdnAddress(user.getEmail());
            String fromAddress = toIdnAddress("sswathika99@outlook.com");
            String senderName = "Multi Insurance Management System";
            String subject = "Please verify your registration";
            String content = "Dear [[name]],<br>"
                    + "Please click the link below to verify your registration:<br>"
                    + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                    + "Thank you,<br>"
                    + "Multi Insurance Management System.";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress.trim());
            helper.setSubject(subject);

            content = content.replace("[[name]]", user.getFirstName());
            String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

            content = content.replace("[[URL]]", verifyURL);

            helper.setText(content, true);

            mailSender.send(message);
            return true;
        } catch (Exception e){
            return false;
        }


    }

    public static String toIdnAddress(String mail) {
        if (mail == null) {
            return null;
        }
        int idx = mail.indexOf('@');
        if (idx < 0) {
            return mail;
        }
        return localPart(mail, idx) + "@" + IDN.toASCII(domain(mail, idx));
    }

    private static String localPart(String mail, int idx) {
        return mail.substring(0, idx);
    }

    private static String domain(String mail, int idx) {
        return mail.substring(idx + 1);
    }

    public boolean verify(String verificationCode) {
        User user = repo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            repo.save(user);

            return true;
        }

    }
}
