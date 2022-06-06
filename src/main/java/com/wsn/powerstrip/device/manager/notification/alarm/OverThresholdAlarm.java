package com.wsn.powerstrip.device.manager.notification.alarm;

import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import com.wsn.powerstrip.device.DAO.AlarmDAO;
import com.wsn.powerstrip.device.DAO.DeviceDAO;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.AlarmTypeEnum;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.device.manager.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 传感器的过载异常作为具体通知者，继承subject类
 * 该报警器不是单例的,其个数与设备数相同, 因此没有并发问题
 * @Author: 夏星毅
 * @Modified : 2021-04-11 18:49 by Wang Zilin
 *
 */
@Slf4j
public class OverThresholdAlarm extends Notification {
    private final Sensor sensor;
    private final HashMap<String, Object> alarmMessageMap = new HashMap<>();
    private final AlarmDAO alarmDAO;
    private final DeviceDAO deviceDAO;


    public OverThresholdAlarm(Sensor sensor,AlarmDAO alarmDAO, DeviceDAO deviceDAO) {
        // 直接发送给有对应权限的用户
        super.sendToUserRole = "admin";
        this.sensor = sensor;
        this.alarmDAO = alarmDAO;
        this.deviceDAO = deviceDAO;
    }

    /**更建议使用of方法创建alarm，因为其更符合语义：xxx的alarm
     * @param sensor 传感器
     * @return 插排对应的alarm
     */
    public static OverThresholdAlarm of(Sensor sensor,AlarmDAO alarmDAO, DeviceDAO deviceDAO) {
        return new OverThresholdAlarm(sensor,alarmDAO,deviceDAO);
    }

    /**
     * 检查设备状态的具体逻辑, 如果出现异常则通知所有观察者
     * @return 检查后设备的最新状态,注意:该方法仅检查设备状态,并不写入到数据库
     */
    public DeviceStateEnum checkDevice() {
        //TODO：这里可以改成只取单一字段，因为每个传感器的功能都是单一的，只取最新数据和threshold相比较就可以了，不需要用map
        ScreenEnum screen = null;
        if(sensor.getPosition() == 1) {
            screen = ScreenEnum.SCREEN1;
        } else if(sensor.getPosition() == 2) {
            screen = ScreenEnum.SCREEN2;
        } else if(sensor.getPosition() == 3) {
            screen = ScreenEnum.SCREEN3;
        } else {
            screen = ScreenEnum.OTHERS;
        }
        SensorHistory sensorHistory = deviceDAO.getLatestHistoryByDidAndScreen(sensor.getId(), screen);
        log.info(String.valueOf(sensorHistory));
        DeviceStateEnum deviceStateEnum = null;

        // 存储处于报警状态的所有传感器
        // 先要查出报警数据库中所有未处理的报警传感器
        Set<String> alarmSensorIdSet = new HashSet<>();
        for(Alarm alarm : alarmDAO.findAllAlarmByHandleState(false, null)) {
            alarmSensorIdSet.add(alarm.getDeviceId());
        }

        // 无数据报警处理
        if(sensorHistory == null) {
            log.debug("无法从设备中获取数据");
//            if(alarmSensorIdSet.contains(sensor.getId())) {
//                List<Alarm> alarmList = alarmDAO.findAlarmsByDeviceId(sensor.getId(), AlarmTypeEnum.NO_DATA, false);
//                for(Alarm newAlarm : alarmList) {
//                    newAlarm.setAlarmLatestTime(LocalDateTime.now());
//                    alarmDAO.updateAlarm(newAlarm);
//                }
//            } else {
//                deviceStateEnum = DeviceStateEnum.ABNORMAL;
//
//                // 将产生一条报警的记录，存入数据库中
//                Alarm alarm = new Alarm();
//                alarm.setAlarmType(AlarmTypeEnum.NO_DATA);
//                alarm.setAlarmFirstTime(LocalDateTime.now());
//                alarm.setDeviceId(sensor.getId());
//                alarm.setDeviceName(sensor.getName());
//                alarm.setDevicePosition(sensor.getPosition());
//                alarm.setDeviceX(sensor.getX());
//                alarm.setDeviceY(sensor.getY());
//                alarm.setAlarmData(-1);
//                alarmDAO.addAlarm(alarm);
//
//                alarmMessageMap.put("id", sensor.getId());
//                alarmMessageMap.put("position", sensor.getAllPosition().toString());
//                alarmMessageMap.put("AlarmType", alarm.getAlarmType().toString());
//                log.info(alarmMessageMap.toString());
//
//                notifyAllObservers();
//            }
            return deviceStateEnum;
        }
        Float sensorResult = sensorHistory.getData();
        Float criticalValue = sensor.getThreshold();

        if (criticalValue == null) {
            log.debug("无法从设备{}中获得用于比较的必要参数,sensorResultMap={},criticalMap={}", sensor.getId(),
                    sensorResult, criticalValue);
            return null;
        }
            // 每种传感器的阈值固定
            if (criticalValue <= sensorResult) {
                // 对于已经产生了一条报警记录但还未处理的传感器，因为它被放入了Set中，
                // 因此不是再新加一条报警记录，而是更新这条报警记录的最新报警时间
                if(alarmSensorIdSet.contains(sensor.getId())) {
                    log.info("传感器数据再次超过阈值！！！");
                    List<Alarm> alarmList = alarmDAO.findAlarmsByDeviceId(sensor.getId(), AlarmTypeEnum.OVER_THRESHOLD, false);
                    for(Alarm newAlarm : alarmList) {
                        newAlarm.setAlarmLatestTime(LocalDateTime.now());
                        alarmDAO.updateAlarm(newAlarm);
                    }
                } else {
                    deviceStateEnum = DeviceStateEnum.ABNORMAL;

                    // 将产生一条报警的记录，存入数据库中
                    Alarm alarm = new Alarm();
                    alarm.setAlarmType(AlarmTypeEnum.OVER_THRESHOLD);
                    alarm.setAlarmFirstTime(LocalDateTime.now());
                    alarm.setDeviceId(sensor.getId());
                    alarm.setDeviceName(sensor.getName());
                    alarm.setDevicePosition(sensor.getPosition());
                    alarm.setDeviceX(sensor.getX());
                    alarm.setDeviceY(sensor.getY());
                    alarm.setAlarmData(sensorResult);
                    alarmDAO.addAlarm(alarm);

                    // 报警消息的内容
                    alarmMessageMap.put("sensorType", sensor.getSensorType());
                    alarmMessageMap.put("position", sensor.getPosition());
                    alarmMessageMap.put("x", sensor.getX());
                    alarmMessageMap.put("y", sensor.getY());

//                    alarmMessageMap.put("AlarmType", alarm.getAlarmType().toString());
//                    alarmMessageMap.put("currentValue", sensorResult.toString());
//                    alarmMessageMap.put("thresholdValue", criticalValue.toString());
                    log.info(alarmMessageMap.toString());

                    notifyAllObservers();
                }
                return deviceStateEnum;
            }


            // 对于在Set中的传感器，数据已经恢复正常了，就将这条报警记录的状态改为已处理
        if(criticalValue > sensorResult && alarmSensorIdSet.contains(sensor.getId())) {
            List<Alarm> alarmList = alarmDAO.findAlarmsByDeviceId(sensor.getId(), AlarmTypeEnum.OVER_THRESHOLD, false);
            for(Alarm newAlarm : alarmList) {
                newAlarm.setHandleState(true);
                newAlarm.setHandleTime(LocalDateTime.now());
                alarmDAO.updateAlarm(newAlarm);
//                Sensor sensor = deviceDAO.findDeviceById(newAlarm.getDeviceId());
//                sensor.setDeviceState(DeviceStateEnum.NORMAL);
//                deviceDAO.updateDevice(sensor);
            }
            deviceStateEnum = DeviceStateEnum.NORMAL;
        }

        if(alarmSensorIdSet.contains(sensor.getId())) {
            List<Alarm> alarmList = alarmDAO.findAlarmsByDeviceId(sensor.getId(), AlarmTypeEnum.NO_DATA, false);
            for(Alarm newAlarm : alarmList) {
                newAlarm.setHandleState(true);
                newAlarm.setHandleTime(LocalDateTime.now());
                alarmDAO.updateAlarm(newAlarm);
            }
            deviceStateEnum = DeviceStateEnum.NORMAL;
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
