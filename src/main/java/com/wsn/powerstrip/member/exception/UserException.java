package com.wsn.powerstrip.member.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 11:18 PM 6/20/2020
 * @Modified By:wangzilinn@gmail.com
 */
public class UserException extends RuntimeException{

    @Getter
    @Setter
    private Integer code;

    public UserException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
