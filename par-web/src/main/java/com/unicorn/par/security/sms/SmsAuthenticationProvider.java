package com.unicorn.par.security.sms;

import com.unicorn.core.exception.ServiceException;
import com.unicorn.par.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class SmsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public LoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        SmsAuthenticationToken regAuthenticationToken = (SmsAuthenticationToken) authentication;

        try {
            loginService.smsLogin(regAuthenticationToken.getLoginInfo());
        } catch (ServiceException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(regAuthenticationToken.getLoginInfo().getPhoneNo());
        SmsAuthenticationToken authenticationToken = new SmsAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationToken.setAuthenticated(true);
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {

        return SmsAuthenticationToken.class == aClass;
    }
}
