package com.wang.common.exception;

public class NoStockException extends RuntimeException{

    public NoStockException(String msg){
        super(msg);
    }
}
