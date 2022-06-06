package com.wsn.powerstrip.device.manager.history.impl;

import com.wsn.powerstrip.common.util.TimeFormatTransUtils;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.manager.history.AbstractHistoryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 胡斐
 * @Modified wangzilinn@gmail.com
 */
/**
@Component
@RequiredArgsConstructor
@Slf4j
public class IotPlatformHistoryHandler extends AbstractHistoryHandler {

    final private IotPlatformContextStrategy iotPlatformContextStrategy;

    @Override
    public List<DeviceHistory> getHistory(Device device, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        //尝试从云平台获取历史:
        IotPlatformService platformService = iotPlatformContextStrategy.getStrategy(device.getIotPlatformType());
        List<IotPlatformHistory> iotPlatformHistoryList = platformService.getHistory(device.getDeviceId(), startDateTime, endDateTime); //可能返回null
        log.debug("从OC取数据,开始时间{}, 结束时间{}, 取了{}条", startDateTime, endDateTime,
                (iotPlatformHistoryList == null) ? 0: iotPlatformHistoryList.size());
        //缓存部分已经对需要的数据进行了初步处理，这里只需要处理
        //1.这里有所有需要的数据，即historyStartTime <= startDateTime
        //2.这里没有所有需要的数据，即historyStartTime > startDateTime
        if (iotPlatformHistoryList != null && iotPlatformHistoryList.size() != 0) {
            //平台数据的起始和结束时间
            LocalDateTime historyStartTime = TimeFormatTransUtils.timestamp2localDateTime(iotPlatformHistoryList.get(0)
                    .buildDeviceHistory(device.getId(), device.getSensorType()).getDateTime().getTime());
            List<DeviceHistory> deviceHistories = new ArrayList<>();    //结果集
            for (IotPlatformHistory iotPlatformHistory : iotPlatformHistoryList) {
                //转换PowerStripHistory
                DeviceHistory foundHistory = iotPlatformHistory.buildDeviceHistory(device.getId(), device.getDeviceType());
                //存入要提交的结果集
                deviceHistories.add(foundHistory);
            }
            // 如果Iot平台中查询到的最早历史记录比请求的历史记录晚,则调用下一个handler请求差的这一部分
            if (historyStartTime.compareTo(startDateTime) > 0){
                deviceHistories.addAll(nextHandler.getHistory(device, startDateTime, historyStartTime));
            }
            return deviceHistories;
        }else if(nextHandler != null){
            //如果无法从云平台获取数据,则直接全部下一个取
            return nextHandler.getHistory(device, startDateTime, endDateTime);
        }else {
            return null;
        }
    }

}
 */
