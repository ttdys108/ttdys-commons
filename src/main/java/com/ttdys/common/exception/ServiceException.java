package com.ttdys.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private String code;
    private String msg;

    public ServiceException() {
        this.code = ErrorCode.SYSTEM_ERROR.getCode();
        this.msg = ErrorCode.SYSTEM_ERROR.getMsg();
    }

    public ServiceException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

}
