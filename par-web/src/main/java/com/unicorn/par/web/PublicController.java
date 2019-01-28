package com.unicorn.par.web;

import com.unicorn.sms.service.MiaodiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private MiaodiService miaodiService;

    @RequestMapping(value = "/sms/verifyCode/login", method = RequestMethod.GET)
    public void sendVerifyCode(String phoneNo) {

        miaodiService.sendVerifyCode(phoneNo, "login");
    }
}
