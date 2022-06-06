package com.wsn.powerstrip.communication.publisher.impl;

import com.wsn.powerstrip.communication.publisher.AbstractAsyncPublisher;
import com.wsn.powerstrip.communication.service.WeChatService;
import com.wsn.powerstrip.device.manager.notification.Notification;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 所有的警报发射器都是单例的，捕获所有设备的警报
 *
 * @Author: 夏星毅
 * @Modified wangzilinn@gmail
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeChatPublisher extends AbstractAsyncPublisher {
    final private WeChatService weChatService;
    final private UserDAO userDAO;

    @Override
    protected void asyncUpdateInternal(Notification notification) {
        /*List<User> targetUsers = userDAO.findUserByRole(notification.getSendToUserRole());
        String content = notification.getAlarmMessage().toString();
        log.debug("自WeChatPublisher发送通知:{}", content);
        for (User user : targetUsers) {
            if (user.getWechatId() != null) {
                // weChatService.pushNotification(user.getWechatId(), content);
            }
        }*/
    }

}
