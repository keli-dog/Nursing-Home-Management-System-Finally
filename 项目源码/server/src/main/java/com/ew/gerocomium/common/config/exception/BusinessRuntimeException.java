package com.ew.gerocomium.common.config.exception;

import com.ew.gerocomium.common.constant.ExceptionEnum;
import lombok.Getter;

@Getter
public class BusinessRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -7382189364622337034L;

    private final Integer code;
    private final String msg;

    public BusinessRuntimeException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessRuntimeException(ExceptionEnum type) {
        this(type.getCode(), type.getMsg());
    }
}
