package com.wsn.powerstrip.communication.POJO.DTO;

import com.wsn.powerstrip.common.util.JsonSerializeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/15/2021 10:16 AM
 */
@Data
@AllArgsConstructor
public class SSEMessage {
    private final String data;
    private final String event;
    private final int retry;

    /**
     * 将notification消息构造为SSE消息
     * @param notificationMessage notification实体
     */
    public SSEMessage(NotificationMessage notificationMessage) {
        this.event = "alarm";
        this.data = JsonSerializeUtil.mapToJson(notificationMessage.getMessageMap());
        this.retry = 1000;
    }

    @Override
    public String toString() {
        return "retry:" + retry + "\n" + "event:" + event + "\n" + "data:" + data + "\n\n";
    }
}
