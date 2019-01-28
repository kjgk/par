package com.unicorn.par.security.sms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginInfo implements Serializable {

    private String phoneNo;

    private String verifyCode;

    private String password;
}
