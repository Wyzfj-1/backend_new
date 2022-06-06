package com.wsn.powerstrip.communication.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/7/2021 6:47 PM
 */
public class CommunicationException extends RuntimeException {
    @Getter
    @Setter
    private Integer code;

    public CommunicationException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
