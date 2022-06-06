package com.wsn.powerstrip.communication.POJO.DTO;

import com.wsn.powerstrip.communication.constant.NotificationMessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/14/2021 9:15 PM
 */
@Data
@AllArgsConstructor
public class NotificationMessage {
    private Map<String, Object> messageMap;
}
