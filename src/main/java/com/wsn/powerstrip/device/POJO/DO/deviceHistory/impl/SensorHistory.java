package com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wsn.powerstrip.common.converter.TimeFormatSerializer;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.constants.SensorType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 2/2/2021 5:34 PM
 * @Edit: chenzhengyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorHistory implements Serializable {
    // @JsonIgnore
    // private final DeviceTypeEnum deviceType = DeviceTypeEnum.SENSOR;
    @Id
    private String id;
    @Schema(description = "历史数据对应设备表的唯一id")
    private String deviceId;
    // 传感器历史应该返回一个Map,因为传感器的返回类型不定,使用固定字段不好扩展
    // private Float leakageCurrent;
    // private Float smokeConcentration;
    // private Float tempData;
    // private Float humData;
    // private Float current;
    // private Float voltage;
    // private Float cumulateDegree;
    private Float data;
    // 便于mongo比较
    @JsonSerialize(using = TimeFormatSerializer.class)
    private Long dateTime;
    private Integer x;
    private Integer y;
}
