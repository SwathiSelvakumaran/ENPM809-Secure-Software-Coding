package com.enpm809w.mims.LoginHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.Common.UserDetail;
import com.enpm809w.mims.Repository.UserRepository;
import com.enpm809w.mims.Services.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        User user = userDetail.getUser();
        if (user.getFailedAttempt() > 0) {
            userDetailService.resetFailedAttempts(user.getEmail());
        }

    }
}
