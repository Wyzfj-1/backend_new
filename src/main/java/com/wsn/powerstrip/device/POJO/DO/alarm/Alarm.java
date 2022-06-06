package com.wsn.powerstrip.device.POJO.DO.alarm;

import com.wsn.powerstrip.device.constants.AlarmTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * User: 珈
 * Time: 2021/10/19  10:26
 * Description:
 * Version:
 */
@Data
public class Alarm  {
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

    @Schema(description = "记录当前报警时传感器的数值")
    private float alarmData;

}
