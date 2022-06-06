package com.wsn.powerstrip.communication.publisher.impl;

import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import com.wsn.powerstrip.communication.POJO.DTO.SmsRecord;
import com.wsn.powerstrip.communication.publisher.AbstractAsyncPublisher;
import com.wsn.powerstrip.communication.service.MessageService;
import com.wsn.powerstrip.communication.service.SmsRecordService;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.device.manager.notification.Notification;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
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
public class MessagePublisher extends AbstractAsyncPublisher {

    final private MessageService messageService;
    final private UserDAO userDAO;
    final private SmsRecordService smsRecordService;
    //这个数组存储每个大屏的短信发送过期时间
    private final Long[] screenSmsTime = new Long[3];

    @Override
    protected void asyncUpdateInternal(Notification notification) {
        List<User> targetUsers = userDAO.findUsersByRole(notification.getSendToUserRole(), null);
        String[] params = new String[4];
        NotificationMessage message = (NotificationMessage) notification.getAlarmMessage().getMessageMap();
        Integer screenPosition = (Integer) message.getMessageMap().get("position");
        //如果该大屏的过期时间小于现在的时间，说明已经过期，则可以发送短信
        if (screenSmsTime[screenPosition - 1] < new Date().getTime()){
            params[0] = "一期" + message.getMessageMap().get("position") + "号";
            params[1] = "" + message.getMessageMap().get("x");
            params[2] = "" + message.getMessageMap().get("y");
            if (message.getMessageMap().get("sensorType") == SensorType.TEMPERATURE) {
                params[3] = "温度";
            }
            params[3] += "设备发生报警";

            //下面是通知每一个管理员，增加短信功能时改成只通知一个管理员
            for (User user : targetUsers) {
                if (user.getPhone() != null)
                    try {
                        smsRecordService.addSmsRecord(new SmsRecord("电器预警中心",new Date(), user.getPhone(),
                                StringUtils.strip(Arrays.toString(params),"[]")));
                        log.info("短信发送成功");
                        messageService.sendPoliticsWarnSms(params,user.getPhone());
                        break;
                    } catch (Exception e) {
                        log.error("短信发送失败");
                        e.printStackTrace();
                    }
            }
            //更新该块大屏的可发送短信过期时间，为更新时间后1h
            screenSmsTime[screenPosition - 1] = DateUtils.addHours(new Date(),1).getTime();
        }


//        for (User user : targetUsers) {
//            if (user.getPhone() != null)
//                try {
//                    messageService.sendPoliticsWarnSms(params,user.getPhone());
//                    log.info("短信发送成功");
//                } catch (Exception e) {
//                    log.error("短信发送失败");
//                    e.printStackTrace();
//                }
//        }
    }
}
