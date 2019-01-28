package com.unicorn.par.security.keep;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unicorn.par.domain.enumeration.RedisKeys;
import com.unicorn.par.service.SignUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;


@Component
public class KeepAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SignUserService signUserService;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String key = null;
        try {
            String loginToken = null;
            String token = new String(Base64Utils.decodeFromString((String) authentication.getPrincipal()));
            key = RedisKeys.AUTO_LOGIN_TOKEN + ":" + token;
            loginToken = redisTemplate.opsForValue().get(key);
            if(!StringUtils.isEmpty(loginToken)){
                JSONObject data = JSON.parseObject(loginToken);
                String accountName = (String) data.get("account");
                String feature = (String) data.get("feature");
                UserDetails userDetails = userDetailsService.loadUserByUsername(accountName);
                if (feature.equals(signUserService.encryptKeepLoginFeature(userDetails.getUsername(), userDetails.getPassword()))) {
                    KeepAuthenticationToken authenticationToken = new KeepAuthenticationToken(userDetails, userDetails.getAuthorities());
                    authenticationToken.setAuthenticated(true);
                    return authenticationToken;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (key != null) {
                redisTemplate.delete(key);
            }
        }
        throw new BadCredentialsException("token无效！");
    }

    @Override
    public boolean supports(Class<?> aClass) {

        return KeepAuthenticationToken.class == aClass;
    }
}
