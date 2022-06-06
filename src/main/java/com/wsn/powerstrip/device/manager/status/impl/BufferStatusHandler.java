package com.wsn.powerstrip.device.manager.status.impl;

import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.manager.status.AbstractStatusHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
@Slf4j
@RequiredArgsConstructor
public class BufferStatusHandler extends AbstractStatusHandler {

    final private StringRedisTemplate stringRedisTemplate;

    @Override
    public DeviceHistory getStatus(Sensor sensor, DeviceHistory deviceHistory) {
        // 当device为虚拟设备（即无deviceId）时直接返回
        // 当传入的最新的历史纪录为null时,说明设备已经有段时间没发消息了,也就没有必要看缓存了
        if (deviceHistory == null || sensor.getId() == null) {
            if (nextHandler != null) {
                return nextHandler.getStatus(sensor, null);
            }
            return null;
        }
        // 如果传入的参数中history不为null,则对比缓存中的数据和传入的history数据,如果缓存中的更新,则返回缓存中的历史
        Date historyDate = deviceHistory.getDateTime();
        // 再从缓存里找(缓存中的key是deviceId,因此首先要判断下设备是否出在同一个云平台):
        /**
        OperationResult operationResult = OperationResult.fromJson(stringRedisTemplate.opsForValue().get(device.getDeviceId()));
        if (operationResult != null && operationResult.getIotPlatformTypeEnum() == device.getIotPlatformType()) {
            log.info("redis has data:");
            log.info(operationResult.getDate().toString());
            log.info(operationResult.getDeviceOperationEnum().toString());
            DeviceOperationEnum operationFromRedis = operationResult.getDeviceOperationEnum();
            Date redisDate = operationResult.getDate();
            if (redisDate.after(historyDate)) {
                // 缓存中的记录比历史中的记录新,采用缓存中的历史记录:
                log.info("use operation from redis:");
                log.info(operationFromRedis.toString());
                ((PowerStripHistory)deviceHistory).setCurrentOperation(operationFromRedis);
            } else {
                // 历史记录中的比缓存中的新:
                // 删除缓存:
                log.info("delete operation");
                stringRedisTemplate.delete(device.getDeviceId());
            }
        }
         */
        if (nextHandler != null) {
            return nextHandler.getStatus(sensor, deviceHistory);
        }else {
            return deviceHistory;
        }
    }
}
