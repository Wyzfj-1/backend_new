package com.wsn.powerstrip.communication.publisher.impl;

import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import com.wsn.powerstrip.communication.publisher.AbstractAsyncPublisher;
import com.wsn.powerstrip.communication.service.SSEEmitterService;
import com.wsn.powerstrip.device.manager.notification.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * UTF-8
 * Created by czy  Time : 2021/4/13 17:16
 *
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SSEPublisher extends AbstractAsyncPublisher {

    private final SSEEmitterService sseEmitterService;

    @Override
    protected void asyncUpdateInternal(Notification notification) {
        NotificationMessage notificationMessage = notification.getAlarmMessage();
        String role = notification.getSendToUserRole();
        log.debug("自SSEPublisher发送的通知：{}, 发送到{}组织", notificationMessage, role);
        /**
         * 组织内用户SSE推送
         */
        sseEmitterService.sendMessage(role, notificationMessage);
    }
}
