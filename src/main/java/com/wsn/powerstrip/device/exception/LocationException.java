package com.wsn.powerstrip.device.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 9:46 PM 6/22/2020
 * @Modified By:wangzilinn@gmail.com
 */
public class LocationException extends RuntimeException{
    @Getter
    @Setter
    private Integer code;

    public LocationException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
