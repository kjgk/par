package com.unicorn.par.config;


import com.unicorn.core.PasswordEncoder;
import com.unicorn.par.security.DefaultLoginProcessingFilter;
import com.unicorn.par.security.keep.KeepAuthenticationProvider;
import com.unicorn.par.security.keep.KeepLoginProcessingFilter;
import com.unicorn.par.security.sms.SmsAuthenticationProvider;
import com.unicorn.par.security.sms.SmsLoginProcessingFilter;
import com.unicorn.par.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

/**
 * 系统模块 — 安全框架配置类
 */
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private SmsAuthenticationProvider smsAuthenticationProvider;

    @Autowired
    private KeepAuthenticationProvider keepAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        if (projectService.getRequiresSecure() != null && projectService.getRequiresSecure()) {
            http.requiresChannel().anyRequest().requiresSecure();
        }

        http
                .addFilterBefore(defaultLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(smsLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(keepLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/dist/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/api/v1/current").permitAll()
                .antMatchers("/api/v1/sign/dict").permitAll()
                .antMatchers("/api/v1/system/file/**").permitAll()
                .antMatchers("/content/image/**").permitAll()
                .antMatchers("/**").hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, exception) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"))
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
                .and()
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
        ;

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(smsAuthenticationProvider);
        auth.authenticationProvider(keepAuthenticationProvider);
    }

    @Bean
    DefaultLoginProcessingFilter defaultLoginProcessingFilter() throws Exception {

        DefaultLoginProcessingFilter processingFilter = new DefaultLoginProcessingFilter("/login/account");
        processingFilter.setAuthenticationManager(authenticationManager());
        processingFilter.setSessionAuthenticationStrategy(new SessionFixationProtectionStrategy());
        processingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        processingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        return processingFilter;
    }

    @Bean
    SmsLoginProcessingFilter smsLoginProcessingFilter() throws Exception {
        SmsLoginProcessingFilter processingFilter = new SmsLoginProcessingFilter("/login/sms");
        processingFilter.setAuthenticationManager(authenticationManager());
        processingFilter.setSessionAuthenticationStrategy(new SessionFixationProtectionStrategy());
        processingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        processingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        return processingFilter;
    }

    @Bean
    KeepLoginProcessingFilter keepLoginProcessingFilter() throws Exception {
        KeepLoginProcessingFilter processingFilter = new KeepLoginProcessingFilter("/login/keep");
        processingFilter.setAuthenticationManager(authenticationManager());
        processingFilter.setSessionAuthenticationStrategy(new SessionFixationProtectionStrategy());
        processingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        processingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        return processingFilter;
    }
}
