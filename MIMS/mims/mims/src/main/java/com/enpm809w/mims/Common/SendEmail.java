package com.enpm809w.mims.Common;

import com.enpm809w.mims.DataConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SendEmail {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DataConstants dataConstants;

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

    public void sendNotificationEmail(Insurance insurance, long expiryDays) throws MessagingException, UnsupportedEncodingException, IOException {
        Path fileName = Path.of("src/main/resources/static/scheduler_notification.txt");

        String toAddress = toIdnAddress("");
        String fromAddress = toIdnAddress("");
        String senderName = "Multi Insurance Management System";
        String subject = "Your insurance is going to expire";
        String content = Files.readString(fileName);


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress.trim());
        helper.setSubject(subject);
        content = content.replace("[[name]]", "User");
        content = content.replace("[[insuranceName]]", insurance.getInsuranceName());
        String renewUrl = insurance.getInsuranceLink();
        String terminateUrl = dataConstants.baseUrl + "/deleteInsurance/" + insurance.getId();
        content = content.replace("[[renewUrl]]", renewUrl);
        content = content.replace("[[terminateUrl]]", terminateUrl);
        content = content.replace("[[days]]", String.valueOf(expiryDays));
        helper.setText(content, true);

        mailSender.send(message);

    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
