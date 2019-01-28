package com.unicorn.par.security.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private LoginInfo loginInfo;

    public SmsAuthenticationToken(LoginInfo loginInfo) {

        super(null);
        this.loginInfo = loginInfo;
        this.principal = loginInfo;
    }

    public SmsAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {

        super(authorities);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {

        return null;
    }

    @Override
    public Object getPrincipal() {

        return principal;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }
}
