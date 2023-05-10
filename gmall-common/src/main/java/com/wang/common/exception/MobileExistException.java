package com.wang.common.exception;

public class MobileExistException extends RuntimeException{

    public MobileExistException() {
        super("手机号已存在!");
    }
}
