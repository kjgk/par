package com.unicorn.par.security.keep;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class KeepAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private final Object credentials;

    public KeepAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {

        super(authorities);
        this.principal = principal;
        this.credentials = "";
    }

    @Override
    public Object getCredentials() {

        return credentials;
    }

    @Override
    public Object getPrincipal() {

        return principal;
    }
}
