package com.mychum1.sookpay.exception;

public class NotValidSprayException extends Exception{

    private Integer code;

    public NotValidSprayException(Integer code, String msg, Throwable e) {
        super(msg, e);
        this.code=code;
    }

    public NotValidSprayException(Integer code, String msg) {
        super(msg);
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
