package com.enpm809w.mims.LoginHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.Common.UserDetail;
import com.enpm809w.mims.Repository.UserRepository;
import com.enpm809w.mims.Services.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        User user = userRepository.findByEmail(email);

        if (user != null) {
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < userDetailService.MAX_FAILED_ATTEMPTS - 1) {
                    userDetailService.increaseFailedAttempts(user);
                } else {
                    userDetailService.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            } else if (!user.isAccountNonLocked()) {
                if (userDetailService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            }
        }

        Model model = null;
        model.addAttribute("errorMessage", "Invalid user credentials");
        super.setDefaultFailureUrl("/error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
