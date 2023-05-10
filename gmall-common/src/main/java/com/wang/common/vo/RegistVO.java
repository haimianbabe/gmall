package com.wang.common.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RegistVO {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 手机验证码
     */
    private String code;

}
