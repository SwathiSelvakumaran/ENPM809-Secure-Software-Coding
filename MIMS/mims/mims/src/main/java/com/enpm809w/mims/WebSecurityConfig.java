package com.enpm809w.mims;

import javax.sql.DataSource;

import com.enpm809w.mims.LoginHandler.LoginFailureHandler;
import com.enpm809w.mims.LoginHandler.LoginSuccessHandler;
import com.enpm809w.mims.Services.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/js/**","/css/**").permitAll()
                .antMatchers("/","/login", "/register", "/changePassword", "/verify").permitAll()
                .antMatchers("/homepage","/showNewInsuranceForm","/showFormForUpdate/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .usernameParameter("email")
                .failureHandler(loginFailureHandler)
                .successHandler(loginSuccessHandler)
                .defaultSuccessUrl("/homepage")
                .permitAll()
                .and()
                .logout().invalidateHttpSession(true).logoutSuccessUrl("/").permitAll();

        http.authenticationProvider(authenticationProvider());

        http.headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'");
        return http.build();
    }

}
