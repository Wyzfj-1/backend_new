package com.wsn.powerstrip.communication.POJO.DTO;

import lombok.Data;

import java.util.Date;

/**
 * @author zxy
 * @Description: 存储短信发送记录的类
 */
@Data
public class SmsRecord {
    private String AlarmPlatform;
    private Date sendDate;
    private String receiver;
    private String smsContent;

    public SmsRecord(String AlarmPlatform, Date sendDate, String receiver, String smsContent) {
        this.AlarmPlatform = AlarmPlatform;
        this.sendDate = sendDate;
        this.receiver = receiver;
        this.smsContent = smsContent;
    }
}
