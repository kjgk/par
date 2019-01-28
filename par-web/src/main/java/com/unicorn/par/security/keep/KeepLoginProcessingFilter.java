package com.unicorn.par.security.keep;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class KeepLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public KeepLoginProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {

        String token = httpServletRequest.getParameter("token");
        KeepAuthenticationToken authenticationToken = new KeepAuthenticationToken(token, null);
        authenticationToken.setDetails(this.authenticationDetailsSource.buildDetails(httpServletRequest));
        return getAuthenticationManager().authenticate(authenticationToken);
    }
}
