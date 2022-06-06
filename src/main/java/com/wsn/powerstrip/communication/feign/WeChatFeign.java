package com.wsn.powerstrip.communication.feign;

import com.wsn.powerstrip.common.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/3/2021 4:04 PM
 */
@FeignClient(name = "weChatFeign", url = "${wechat.url}", configuration = FeignConfig.class)
public interface WeChatFeign {
    /**
     * @param wechatId wechatid
     * @param content 发送的内容
     * @return 返回结果
     */
    @GetMapping
    @ResponseBody
    String sendNotification(@RequestParam(value = "userId") String wechatId, @RequestParam(value = "content") String content);
}
