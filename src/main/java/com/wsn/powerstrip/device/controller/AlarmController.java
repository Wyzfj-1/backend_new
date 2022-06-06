package com.wsn.powerstrip.device.controller;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.annotation.WebLog;
import com.wsn.powerstrip.communication.service.SmsRecordService;
import com.wsn.powerstrip.device.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * User: 珈
 * Time: 2021/10/21  18:07
 * Description:
 * Version:
 */
@RestController
@RequestMapping("/api/alarm")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "Alarm", description = "报警管理接口,CRUD等等")
public class AlarmController {

    final private AlarmService alarmService;
    final private SmsRecordService smsRecordService;

    @GetMapping("/smsRecords")
    @Operation(summary = "返回预警短信的记录。", description = "必须传入查询的页数和每页的长度。")
    public Response<?> getSmsRecords(@Parameter(description = "查询的页数")
                                     @Range(min = 1)
                                     @RequestParam(value = "page") Integer page,
                                     @Parameter(description = "每页的长度")
                                     @Range(min = 1)
                                     @RequestParam(value = "limit", defaultValue = "10") Integer limit){
        return new Response<>(smsRecordService.getSmsRecords(new QueryPage(page,
                limit)));
    }

    @GetMapping("/countAll")
    @WebLog
    @Operation(summary = "历史报警总数",
            description = "统计所有报警记录个数，包括已处理和未处理")
    public Response<?> countAll() {
        int count = (int)alarmService.getNumberOfAlarm();
        return new Response<>(count);
    }

    @GetMapping("/countNotHandled")
    @WebLog
    @Operation(summary = "当前报警总数",
            description = "统计未处理的报警记录个数")
    public Response<?> countNotHandled() {
        int count = (int)alarmService.getNumberOfHandledAlarm(false);
        return new Response<>(count);
    }

    @GetMapping("/countNotHandledByPosition")
    @WebLog
    @Operation(summary = "不同大屏的当前报警数",
            description = "需传入几号大屏")
    public Response<?> countNotHandledByPosition(@Parameter(description = "几号大屏", example = "1")
                                                     @RequestParam("id") int position) {
        int count = (int)alarmService.getNumberOfAlarmByPosition(position);
        return new Response<>(count);
    }

    @GetMapping("/countMonthAlarm")
    @WebLog
    @Operation(summary = "每个月份的报警总数和已处理报警数",
            description = "需传入年和月,例如：年：2021,月：1")
    public Response<?> countMonthAlarm(@Parameter(description = "年", example = "2021")
                                          @RequestParam("year") int year,
                                      @Parameter(description = "月", example = "10")
                                      @Range(min = 1,max = 12)
                                      @RequestParam("month") int month) {
        Map<String,Integer> map=new HashMap<>();
        int alarm_num = (int)alarmService.getNumberOfAlarmByMonth(year,month);
        System.out.println("\n\n"+"alarm_num:"+alarm_num);
        int handledAlarm_num = (int)alarmService.getNumberOfHandledAlarmByMonth(year,month);
        System.out.println("\n\n"+"handledAlarm_num"+handledAlarm_num);
        map.put("alarm",alarm_num);
        map.put("handledAlarm",handledAlarm_num);
        return new Response<>(map);
    }

    @GetMapping("/countPastYearAlarm")
    @WebLog
    @Operation(summary = "过去一年每个月份的报警总数和已处理报警数",
            description = "需传入年,例如：年：2020,返回map的键值对为字符串")
    public Response<?> countPastYearAlarm(@Parameter(description = "年", example = "2021")
                                       @RequestParam("year") int year){
        List<Map<String, String>> listMaps = new ArrayList<>();
        for (int month= 1 ; month <= 12 ; month++){
            Map<String,String> map=new HashMap<>();
            int alarm_num = (int)alarmService.getNumberOfAlarmByMonth(year,month);
            System.out.println("\n\n"+"alarm_num:"+alarm_num);
            int handledAlarm_num = (int)alarmService.getNumberOfHandledAlarmByMonth(year,month);
            System.out.println("\n\n"+"handledAlarm_num"+handledAlarm_num);
            map.put("dateTime",year + "-" + month);
            map.put("alarm",alarm_num+"");
            map.put("handledAlarm",handledAlarm_num+"");
            listMaps.add(map);
        }
        return new Response<>(listMaps);
    }


    @GetMapping("/notHandleList")
    @WebLog
    @Operation(summary = "未处理告警信息列表",
            description = "位置x,y都为-1时为该设备未找到")
    public Response<?> NotHandleList(@Parameter(description = "查询的页数")
                                           @Range(min = 1)
                                           @RequestParam(value = "page") int page,
                                       @Parameter(description = "每页的长度")
                                           @Range(min = 1)
                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {

        return new Response<>(alarmService.findAllAlarmByHandleState
                (false,new QueryPage(page,limit)));
    }

    @GetMapping("/handleList")
    @WebLog
    @Operation(summary = "已处理告警信息列表",
            description = "开始时间和结束时间未输入时默认为null,位置x,y都为-1时为该设备未找到")
    public Response<?>HandleList(@Parameter(description = "查询的开始时间",example = "2021-10-21 20:00:00")
                                     @RequestParam(value = "startTime", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
                                             Date startTime,
                                 @Parameter(description = "查询的结束时间",example = "2021-10-21 22:00:00")
                                     @RequestParam(value = "endTime", required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME)
                                             Date endTime,
                                 @Parameter(description = "大屏位置,-1为该值未传入")
                                     @RequestParam(value = "position", required = false,defaultValue = "-1") int position,
                                 @Parameter(description = "查询的页数")
                                     @Range(min = 1)
                                     @RequestParam(value = "page",defaultValue = "1") int page,
                                 @Parameter(description = "每页的长度")
                                     @Range(min = 1)
                                     @RequestParam(value = "limit", defaultValue = "10") int limit) {

        //未输入position
        if (position != 0 && position != 1 && position != 2 && position != 3) {
            return new Response<>(alarmService.findHandledAlarmByTime
                    (startTime,endTime,new QueryPage(page, limit)));
        }
        //输入position
        else {
            return new Response<>(alarmService.findHandledAlarmByTimeAndPos
                    (position,startTime,endTime,new QueryPage(page, limit)));
        }
    }

}
