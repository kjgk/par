package com.unicorn.par.service;

import com.unicorn.core.exception.ServiceException;
import com.unicorn.par.domain.po.SignUser;
import com.unicorn.par.security.sms.LoginInfo;
import com.unicorn.sms.service.MiaodiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LoginService {

    @Autowired
    private MiaodiService miaodiService;

    @Autowired
    private SignUserService signUserService;

    public void smsLogin(LoginInfo registerInfo) {

        String phoneNo = registerInfo.getPhoneNo();
        String tunnel = "login";
        String verifyCode = miaodiService.getVerifyCode(phoneNo, tunnel);
        if (StringUtils.isEmpty(verifyCode) || !verifyCode.equals(registerInfo.getVerifyCode())) {
            throw new ServiceException("验证码不正确!");
        }

        // 清空验证码
        miaodiService.removeVerifyCode(phoneNo, tunnel);

        SignUser signUser = signUserService.getByPhoneNo(phoneNo);
        if (signUser == null) {
            // 如果用户不存在，则根据手机号创建新用户
            signUserService.createByPhoneNo(phoneNo);
        }
    }
}
