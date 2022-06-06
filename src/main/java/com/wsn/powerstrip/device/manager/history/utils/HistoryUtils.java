package com.wsn.powerstrip.device.manager.history.utils;

import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.exception.DeviceDAOException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HistoryUtils {
    /**
     * 对传入的历史记录进行重采样
     * @param originalHistoryList 未经过重采样的历史记录
     * @param period 重采样之后的时间间隔(可以是null:不进行重采样, minute:重采样为1minute, hour: 重采样为1hour
     * @return 重采样的结果
     */
    @SuppressWarnings("all")
    public static List<? extends DeviceHistory> resampleHistoryList(List<? extends DeviceHistory> originalHistoryList, TimeUnit period) {
        log.debug("正在进行重采样, 原数据长度为{}, 目标采样周期为{}", originalHistoryList.size(), period);
        if (period == null) {
            return originalHistoryList;
        }
        long plusSecond;
        switch (period) {
            case MINUTES:
                plusSecond = 60;
                break;
            case HOURS:
                plusSecond = 60 * 60;
                break;
            default:
                throw new DeviceDAOException(500, "不支持的重采样");
        }
        ArrayList<DeviceHistory> result = new ArrayList<>();
        Instant slotEndInstant = originalHistoryList.get(0).getDateTime().toInstant().plusSeconds(plusSecond);
        int index = 0;
        while (index < originalHistoryList.size()) {
            ArrayList<DeviceHistory> historiesInSlot = new ArrayList<>();
            while (index < originalHistoryList.size() && originalHistoryList.get(index).getDateTime().toInstant().isBefore(slotEndInstant)) {
                DeviceHistory deviceHistory = originalHistoryList.get(index);
                historiesInSlot.add(deviceHistory);
                index++;
            }
            DeviceHistory averageHistory;
            if (historiesInSlot.size() != 0) {
                // TODO:这里只是取了中间的那个作为平均值,其实应该手动算这个slot内所有历史记录的平均的,但是这里用到了多态,不知道传入的
                // 是什么类型,所以写起来非常麻烦, 暂不实现
                averageHistory = historiesInSlot.get(historiesInSlot.size() / 2);
            }else {
                averageHistory = result.get(result.size() - 1);
            }
            result.add(averageHistory);
            slotEndInstant = slotEndInstant.plusSeconds(60);
        }
        return result;
    }
}
