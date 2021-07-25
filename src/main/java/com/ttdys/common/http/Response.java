package com.ttdys.common.http;

import lombok.Getter;
import lombok.Setter;

import static com.ttdys.common.exception.ErrorCode.SUCCESS;

@Getter
@Setter
public class Response<T> {

    private String code;
    private String msg;

    private T data;

    public boolean succeed() {
        return SUCCESS.getCode().equals(code);
    }
}
