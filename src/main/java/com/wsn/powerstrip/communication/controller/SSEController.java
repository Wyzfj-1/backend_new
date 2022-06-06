package com.wsn.powerstrip.communication.controller;

import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.communication.POJO.DTO.SmsRecord;
import com.wsn.powerstrip.communication.service.SSEEmitterService;
import com.wsn.powerstrip.communication.service.SmsRecordService;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * UTF-8
 * Created by czy  Time : 2021/4/13 16:53
 *
 * @version 1.0
 */
@Controller
@RequestMapping("/api/push")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SSEController {
    final private SSEEmitterService sseEmitterService;

    /**
     * 直接在这个接口返回 SseEmitter对象，数据是直接从这个接口出去的。
     * 推送
     * @param role
     * @param userId
     * @return
     */
    @GetMapping(value = "/subscribe")
    @Operation(
            summary = "通过这个接口订阅发送来的异步报警信息"
    )
    public SseEmitter subscribe(@RequestParam(value = "userRole") String role, @RequestParam(value =
            "userId") String userId) {
        return sseEmitterService.connect(role, userId);
    }

    @GetMapping(value = "/unsubscribe")
    @ResponseBody
    @Operation(
            summary = "取消报警消息的订阅"
    )
    public Response<?> unsubscribe(@RequestParam(value = "userRole") String role, @RequestParam(value =
            "userId") String userId) {
        sseEmitterService.removeSubscriber(role, userId);
        return new Response<>();
    }


}
