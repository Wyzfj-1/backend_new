package com.wsn.powerstrip.device.manager.status.impl;

import com.wsn.powerstrip.device.DAO.DeviceHistoryDAO;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.manager.status.AbstractStatusHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
@Component
@RequiredArgsConstructor
public class DbStatusHandler extends AbstractStatusHandler {

    final private DeviceHistoryDAO deviceHistoryDAO;

    @Override
    public DeviceHistory getStatus(Device device, DeviceHistory deviceHistory) {
        // 当前handler应该是更新最慢的handler,其数据也最老,因此无需与传入的powerStripHistory比较, 仅当传入的handler为null时从数据库查询
        if (deviceHistory == null) {
            LocalDateTime endDateTime = LocalDateTime.now();
            LocalDateTime startDateTime = endDateTime.minusMinutes(1);
            startDateTime = LocalDateTime.of(2020, 10, 20, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond());
            endDateTime = LocalDateTime.of(2020, 10, 20, endDateTime.getHour(), endDateTime.getMinute(), endDateTime.getSecond());
            Date startDate = Date.from(startDateTime.toInstant(ZoneOffset.ofHours(8)));
            Date endDate = Date.from(endDateTime.toInstant(ZoneOffset.ofHours(8)));
            List<? extends DeviceHistory> histories = deviceHistoryDAO.findDeviceHistoryByRange(device.getId(), startDate, endDate,device.getSensorType().getDeviceHistoryClass());
            deviceHistory = (histories == null || histories.size() == 0) ? null : histories.get(0);
        }
        if (nextHandler != null) {
            return nextHandler.getStatus(device, deviceHistory);
        }else {
            return deviceHistory;
        }

    }
}
 */
