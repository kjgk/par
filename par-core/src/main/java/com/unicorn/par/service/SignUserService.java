package com.unicorn.par.service;

import com.alibaba.fastjson.JSON;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.unicorn.par.domain.enumeration.RedisKeys;
import com.unicorn.par.domain.po.QSignUser;
import com.unicorn.par.domain.po.SignUser;
import com.unicorn.par.repository.SignUserRepository;
import com.unicorn.system.domain.po.Account;
import com.unicorn.system.domain.po.Role;
import com.unicorn.system.domain.po.User;
import com.unicorn.system.domain.po.UserRole;
import com.unicorn.system.repository.RoleRepository;
import com.unicorn.system.service.AccountService;
import com.unicorn.system.service.UserService;
import com.unicorn.utils.Identities;
import com.unicorn.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class SignUserService {

    @Autowired
    private SignUserRepository signUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public SignUser getCurrentSignUser() {

        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return getByUser(currentUser);
    }

    public SignUser getByUser(User user) {

        return signUserRepository.findByUserObjectId(user.getObjectId());
    }

    public SignUser getSignUser(Long objectId) {

        return signUserRepository.get(objectId);
    }

    public SignUser getByPhoneNo(String phoneNo) {

        QSignUser qSignUser = QSignUser.signUser;
        BooleanExpression expression = qSignUser.phoneNo.eq(phoneNo);
        List<SignUser> userList = signUserRepository.findAll(expression);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    public void createByPhoneNo(String phoneNo) {

        Role role = roleRepository.findByTag("Normal");
        UserRole userRole = new UserRole();
        userRole.setRole(role);

        // 保存到User表
        User user = new User();
        user.setName(phoneNo);
        user.setUserRoleList(Arrays.asList(userRole));
        user = userService.saveUser(user);

        // 保存到帐号表
        Account account = new Account();
        account.setPassword(phoneNo.substring(5));
        account.setUser(user);
        account.setName(phoneNo);
        accountService.saveAccount(account);

        // 保存到SignUser表
        SignUser signUser = new SignUser();
        signUser.setPhoneNo(phoneNo);
        signUser.setUser(user);
        signUserRepository.save(signUser);
    }


    /**
     * 保存登录令牌
     */
    public String saveKeepLoginToken(String account, String password) {

        String token = Identities.uuid();
        Map data = new HashMap();
        data.put("account", account);
        data.put("feature", encryptKeepLoginFeature(account, password));
        redisTemplate.opsForValue().set(RedisKeys.AUTO_LOGIN_TOKEN + ":" + token, JSON.toJSONString(data), 90, TimeUnit.DAYS);
        return token;
    }

    /**
     * 获取帐号密码特征
     */
    public String encryptKeepLoginFeature(String accountName, String password) {

        return Md5Utils.encrypt(accountName + "|" + password);
    }

    public boolean isSignManager() {
        SignUser signUser = getCurrentSignUser();
        if (signUser == null || signUser.getUser() == null) {
            return false;
        }
        boolean isManager = false;
        for (UserRole userRole : signUser.getUser().getUserRoleList()) {
            if (userRole.getRole().getTag().equalsIgnoreCase("Manager")) {
                isManager = true;
                break;
            }
        }
        return isManager;
    }
}
