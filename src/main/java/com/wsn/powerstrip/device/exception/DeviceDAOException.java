package com.wsn.powerstrip.device.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 11:22 PM 6/20/2020
 * @Modified By:wangzilinn@gmail.com
 */
public class DeviceDAOException extends RuntimeException {
    @Getter
    @Setter
    private Integer code;

    public DeviceDAOException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
