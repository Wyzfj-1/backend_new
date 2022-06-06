package com.wsn.powerstrip.device.manager.status.impl;

import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.manager.status.AbstractStatusHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/**
@Slf4j
@Component
@RequiredArgsConstructor
public class IotPlatformStatusHandler extends AbstractStatusHandler {

    final private IotPlatformContextStrategy iotPlatformContextStrategy;


    @Override
    public DeviceHistory getStatus(Device device, DeviceHistory deviceHistory) {
        //这一般是责任链中第一个handler,因此不需要判断
        // log.debug("IotPlatformStatusHandler {}", simpleDevice.toString());
        IotPlatformHistory iotPlatformHistory = iotPlatformContextStrategy.getStrategy(device.getIotPlatformType())
                .getLatestStatus(device.getId());
        if (iotPlatformHistory != null) {
            deviceHistory = iotPlatformHistory.buildDeviceHistory(device.getId(), device.getSensorType());
        }
        if (nextHandler != null) {
            return nextHandler.getStatus(device, deviceHistory);
        }else {
            return deviceHistory;
        }
    }
}
 */
