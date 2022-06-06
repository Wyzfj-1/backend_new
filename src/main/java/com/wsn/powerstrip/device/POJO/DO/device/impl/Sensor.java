package com.wsn.powerstrip.device.POJO.DO.device.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.device.manager.status.AbstractStatusHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 传感器实体
 * @Author: 夏星毅
 * @Date: 9/15/2021 4:38 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    @Id
    private String id;


    @Schema(description = "传感器类型")
    private SensorType sensorType;
    @Schema(description = "传感器的名字")
    private String name;

    @Schema(description = "设备当前状态(掉线,正常,报废)")
    private DeviceStateEnum deviceState;

    @Schema(description = "传感器的加入时间")
    private LocalDateTime createTime;

    @Schema(description = "传感器的报废时间")
    private LocalDateTime scrappedTime;

    @Schema(description = "传感器具体的位置信息，分为一号屏，二号屏，三号屏和其他零号位置")
    private Integer position;

    @Schema(description = "传感器的位置坐标，在大屏上的横坐标或者是一期还是二期")
    private Integer x;

    @Schema(description = "传感器的位置坐标，在大屏上的纵坐标或者是一期二期的某个位置")
    private Integer y;

    @Schema(description = "设置传感器数据的阈值")
    private Float threshold;

    @Schema(description = "设置传感器设备的使用期限")
    private float criticalUseTime;


    //以上字段储存在数据库中, 以下字段在运行时注入
    @JsonIgnore
    @Transient
    private AbstractStatusHandler statusHandler;

    public Sensor(String s, String s1, DeviceStateEnum normal, LocalDateTime utc, SensorType temperature, LocalDateTime utc1, int i, int i1, int j, float v, float v1) {
    }


    public Map<String, Integer> getAllPosition() {
        Map<String, Integer> map = new HashMap<>();
        map.put("x:", this.x);
        map.put("y:", this.y);
        map.put("position", this.position);
        return map;
    }
}
