package com.wsn.powerstrip.device.manager.notification.alarm;

import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import com.wsn.powerstrip.device.DAO.AlarmDAO;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.constants.AlarmTypeEnum;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.device.manager.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class OverTimeAlarm extends Notification {
    private final Sensor sensor;
    private final HashMap<String, Object> alarmMessageMap = new HashMap<>();
    private final AlarmDAO alarmDAO;

    public OverTimeAlarm(Sensor sensor,AlarmDAO alarmDAO) {
        super.sendToUserRole = "admin";
        this.sensor = sensor;
        this.alarmDAO = alarmDAO;
    }

    /**
     * @param sensor 设备实体
     * @return 设备对应的时间alarm
     */
    public static OverTimeAlarm of(Sensor sensor,AlarmDAO alarmDAO) {
        return new OverTimeAlarm(sensor,alarmDAO);
    }

    public DeviceStateEnum checkDevice() {
        // TODO:所有类型设备
        DeviceStateEnum deviceStateEnum = null;
        Date now = new Date();
        long nowTime = now.getTime();
        long currentUseTime = (nowTime - sensor.getCreateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())/(1000*60*60*24);
        Float criticalUseTime = sensor.getCriticalUseTime();
        if(criticalUseTime == null) {
            log.debug("无法从设备{}中获得用于比较的必要参数,criticalOnlineTime={}", sensor.getId(),
                    criticalUseTime);
            return null;
        }
        if (criticalUseTime < currentUseTime) {
            deviceStateEnum = DeviceStateEnum.SCRAPPED;

            // 将产生一条报警的记录，存入数据库中
            Alarm alarm = new Alarm();
            alarm.setAlarmType(AlarmTypeEnum.OVER_TIME);
            alarm.setAlarmFirstTime(LocalDateTime.now());
            alarm.setDeviceId(sensor.getId());
            alarm.setDeviceName(sensor.getName());
            alarm.setDevicePosition(sensor.getPosition());
            alarm.setDeviceX(sensor.getX());
            alarm.setDeviceY(sensor.getY());
            alarmDAO.addAlarm(alarm);

            // 报警消息的内容
            alarmMessageMap.put("sensorType", sensor.getSensorType());
            alarmMessageMap.put("position", sensor.getPosition());
            alarmMessageMap.put("x", sensor.getX());
            alarmMessageMap.put("y", sensor.getY());

            log.info(alarmMessageMap.toString());

            notifyAllObservers();
        }
        return deviceStateEnum;
    }

    @Override
    public NotificationMessage getAlarmMessage() {
        return new NotificationMessage(alarmMessageMap);
    }

    public String getDeviceDbId() {
        return sensor.getId();
    }

    public SensorType getSensorType() {
        return sensor.getSensorType();
    }
}
