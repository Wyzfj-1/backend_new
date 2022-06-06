package com.wsn.powerstrip.communication.service;

import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 4/14/2021 5:32 PM
 */
public interface SSEEmitterService {

    SseEmitter connect(String organizationId, String userId);

    void sendMessage(String organizationId, NotificationMessage notificationMessage);

    void removeSubscriber(String organizationId, String userId);

    int getSubscribeCount();

}
