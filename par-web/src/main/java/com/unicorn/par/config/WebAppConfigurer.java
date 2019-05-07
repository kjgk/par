package com.unicorn.par.config;

import com.alibaba.fastjson.JSON;
import com.unicorn.core.domain.po.User;
import com.unicorn.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统模块 — MVC配置类
 */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    @Bean(name = "authenticationSuccessHandler")
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                Map userData = new HashMap();
                Map result = new HashMap();
                User user = userService.getCurrentUser();
                userData.put("id", user.getObjectId());
                userData.put("username", user.getName());
                if (!CollectionUtils.isEmpty(user.getUserRoleList())) {
                    userData.put("roleTag", user.getUserRoleList().get(0).getRole().getTag());
                } else {
                    userData.put("roleTag", "Anonymous");
                }
                result.put("success", true);
                result.put("user", userData);
                result.put("session", Base64Utils.encodeToString(request.getSession().getId().getBytes()));
                response.getWriter().print(new String(JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            }
        };
    }

    @Bean(name = "authenticationFailureHandler")
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                String message = exception.getMessage();
                if (exception instanceof UsernameNotFoundException) {
                    message = "用户不存在!";
                } else if (exception instanceof BadCredentialsException) {
                    message = "帐号密码错误，请重新输入!";
                }
                Map result = new HashMap();
                result.put("message", message);
                result.put("success", false);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(JSON.toJSONString(result));
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            if ("application/json".equals(httpServletRequest.getHeader("accept"))) {
                httpServletResponse.getWriter().print("{\"success\": true}");
            } else {
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath());
            }
        };
    }
}
