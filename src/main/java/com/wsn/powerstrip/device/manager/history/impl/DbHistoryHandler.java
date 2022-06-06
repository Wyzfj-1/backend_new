package com.wsn.powerstrip.device.manager.history.impl;

import com.wsn.powerstrip.device.DAO.DeviceHistoryDAO;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.manager.history.AbstractHistoryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * @Author: 胡斐
 * @Modified wangzilinn@gmail.com
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DbHistoryHandler extends AbstractHistoryHandler {
    final private DeviceHistoryDAO deviceHistoryDAO;

    /*@Override
    public List<DeviceHistory> getHistory(Device simpleDevice, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 说明该云平台不支持获取历史,尝试从本地获取
        Date startDate = Date.from(startDateTime.toInstant(ZoneOffset.UTC));  //这里是本地时区
        Date endDate = Date.from(endDateTime.toInstant(ZoneOffset.UTC));
        // deviceHistoryDAO.findDeviceHistoryByRange()
        List<? extends DeviceHistory> deviceHistories = deviceHistoryDAO.findDeviceHistoryByRange(simpleDevice.getId(),
                startDate, endDate, simpleDevice.getSensorType().getDeviceHistoryClass());
        if (deviceHistories != null) {
            //当前handler能处理,则直接返回处理内容
            return (List<DeviceHistory>) deviceHistories;
        } else if (nextHandler != null) {
            //当前handler不能处理,且定义了下一个handler,移交给下一个处理
            return nextHandler.getHistory(simpleDevice, startDateTime, endDateTime);
        }else {
            //当前handler不能处理,且没有定义下一个handler,则返回null
            return null;
        }
    }*/
    @Override
    public List<SensorHistory> getHistory(ScreenEnum screen, Sensor sensor, Long start, Long end) {
        // deviceHistoryDAO.findDeviceHistoryByRange()
        List<? extends SensorHistory> deviceHistories = deviceHistoryDAO.findSensorHistoryByRange(screen, sensor.getId(), start, end, SensorHistory.class);
        if (deviceHistories != null) {
            //当前handler能处理,则直接返回处理内容
            return (List<SensorHistory>) deviceHistories;
        } else if (nextHandler != null) {
            //当前handler不能处理,且定义了下一个handler,移交给下一个处理
            return nextHandler.getHistory(screen, sensor, start, end);
        }else {
            //当前handler不能处理,且没有定义下一个handler,则返回null
            return null;
        }
    }

}

