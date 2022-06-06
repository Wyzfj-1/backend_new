package com.wsn.powerstrip.common.controller;

import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.annotation.WebLog;
import com.wsn.powerstrip.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 1:07 PM 6/18/2020
 * @Modified By:wangzilinn@gmail.com
 */
@RestController
@RequestMapping("/test")
@Slf4j
@Tag(name = "Test", description = "测试用接口,如手动开关是否将设备历史放入数据库中")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {

    final private DeviceService deviceService;


    @GetMapping
    @Operation(summary = "这是一个测试连接, 如果有输出,则说明服务正在运行")
    public Response<?> test() {
        return new Response<>("这是一个测试连接, 如果有输出,则说明服务正在运行");
    }




    @GetMapping("/3")
    public Response<?> test3() {
        return new Response<>();
    }

    @GetMapping("/4")
    public Response<?> test4() {

        return new Response<>("已获得连接到Tuya的token");
    }

    @GetMapping("/5")

    public Response<?> test5() {

        return new Response<>("获取设备最新数据");
    }

//    @GetMapping("/8")
//    @Operation(summary = "设置插排温度")
//    public void testUpdateHistory(){
//        powerStripService.updatePowerStripHistory();
//    }

//    @GetMapping("/9")
//    public Response<?> test9() {
//        IotPlatformHistory e01 = mqttPlatformService.getLatestStatus("e01");
//
//        return new Response<>(e01.toString());
//    }



}
