package com.wsn.powerstrip.device.POJO.DTO.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 9:57 AM 08/19/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSummaryResponse {
    private long numOfDevice;
    private long numOfNormalDevice;
    private long numOfScrappedDevice;
    private long numOfAbnormalDevices;
    private long numOfSmokeSensor;
    private long numOfTempSensor;
    private long numOfLeakageSensor;
    private long numOfElectricMeterSensor;
}
