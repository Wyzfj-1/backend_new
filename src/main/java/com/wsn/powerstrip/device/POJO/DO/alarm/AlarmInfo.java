package com.wsn.powerstrip.device.POJO.DO.alarm;

import com.wsn.powerstrip.device.constants.AlarmTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * User: 珈
 * Time: 2021/11/2  20:01
 * Description:用于返回前端的报警信息
 * Version:
 */
@Data
public class AlarmInfo {
    @Id
    private String id;

    @Schema(description = "报警设备Id")
    private String deviceId;

    @Schema(description = "报警设备名称")
    private String deviceName;

    @Schema(description = "报警设备所处位置，几号大屏")
    private int devicePosition;

    @Schema(description = "报警的类型")
    private AlarmTypeEnum alarmType;

    @Schema(description = "首次报警时间")
    private LocalDateTime alarmFirstTime;

    @Schema(description = "最新报警时间")
    private LocalDateTime alarmLatestTime;

    @Schema(description = "报警的处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "报警是否被处理")
    private boolean handleState;

    @Schema(description = "传感器的位置坐标，在大屏上的横坐标或者是一期还是二期")
    private Integer deviceX;

    @Schema(description = "传感器的位置坐标，在大屏上的纵坐标或者是一期二期的某个位置")
    private Integer deviceY;

    public AlarmInfo() {
    }


    public AlarmInfo(Alarm alarm) {
        this.id = alarm.getId();
        this.deviceId = alarm.getDeviceId();
        this.deviceName = alarm.getDeviceName();
        this.devicePosition = alarm.getDevicePosition();
        this.alarmType = alarm.getAlarmType();
        this.alarmFirstTime = alarm.getAlarmFirstTime();
        this.alarmLatestTime = alarm.getAlarmLatestTime();
        this.handleTime = alarm.getHandleTime();
        this.handleState = alarm.isHandleState();
        this.deviceX = -1;
        this.deviceY = -1;
    }
}