package com.wsn.powerstrip.communication.service.impl;

import com.wsn.powerstrip.communication.service.WeChatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/4/2021 11:33 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WeChatServiceImplTest {

    @Autowired
    WeChatService weChatService;
    @Test
    public void pushNotification() {
        weChatService.pushNotification("327", "推送测试");
    }
}