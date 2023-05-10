package com.wang.common.exception;

public class UserExistException extends RuntimeException{

    public UserExistException() {
        super("用户已存在!");
    }
}
