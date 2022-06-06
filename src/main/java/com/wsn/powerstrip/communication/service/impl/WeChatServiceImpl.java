package com.wsn.powerstrip.communication.service.impl;

import com.wsn.powerstrip.communication.feign.WeChatFeign;
import com.wsn.powerstrip.communication.service.WeChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/3/2021 8:01 PM
 */
@Service
@RequiredArgsConstructor
public class WeChatServiceImpl implements WeChatService {
    final private WeChatFeign weChatFeign;
    @Override
    public void pushNotification(String wechatId, String content) {
        weChatFeign.sendNotification(wechatId, content);
    }
}
