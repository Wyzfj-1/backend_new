package com.wsn.powerstrip.communication.service;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/3/2021 4:00 PM
 */
public interface WeChatService {
    void pushNotification(String wechatId, String content);
}
