package com.wsn.powerstrip.communication.publisher.impl;

import com.wsn.powerstrip.common.util.JsonSerializeUtil;
import com.wsn.powerstrip.communication.publisher.AbstractAsyncPublisher;
import com.wsn.powerstrip.communication.service.MailService;
import com.wsn.powerstrip.device.manager.notification.Notification;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;

/**
 * 所有的警报发射器都是单例的，捕获所有设备的警报
 *
 * @Author: 夏星毅
 * @Modified wangzilinn@gmail.com
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MailPublisher extends AbstractAsyncPublisher {
    final private MailService mailService;
    final private UserDAO userDAO;

    @Override
    protected void asyncUpdateInternal(Notification notification) {
        /*List<User> targetUsers = userDAO.findUserByRole(notification.getSendToUserRole());
        for (User user : targetUsers) {
            if (user.getEmail() != null) {
                String content = JsonSerializeUtil.mapToJson(notification.getAlarmMessage().getMessageMap());
                try {
                    mailService.sendMail(user.getEmail(), "传感器异常提醒", content);
                } catch (MessagingException e) {
                    log.error("邮件发送失败");
                    e.printStackTrace();
                }
            }
        }*/
    }
}
