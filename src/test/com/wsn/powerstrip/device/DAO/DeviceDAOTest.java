package com.wsn.powerstrip.device.DAO;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.communication.service.impl.MessageServiceImpl;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class DeviceDAOTest {
    @Autowired
    DeviceDAO deviceDAO;

    @Autowired
    MessageServiceImpl messageService;

    @Test
    public void addDevice() {
        Sensor sensor = new Sensor();
        sensor.setName("测试添加传感器");
        sensor.setCreateTime(LocalDateTime.now(ZoneId.of("UTC")));
        deviceDAO.addDevice(sensor);
    }

    @Test
    public void sendV() {
        String[] params = {"一期1号","1","2","温度"};

        messageService.sendVerificationCode("923213","13281117116");
        // messageService.sendPoliticsWarnSms(params,"13281117116");
    }

    @Test
    public void deleteDevice() {
        deviceDAO.deleteById("617663236091656f5ee79fd7");
    }

    // TODO: 设备更新存在问题
    @Test
    public void findDeviceById() {
        Sensor sensor = deviceDAO.findDeviceById("6178027fe5a5c22ccc5deb5b");
        System.out.println(sensor);
        sensor.setThreshold(55.0F);
        deviceDAO.updateDevice(sensor);
        System.out.println(deviceDAO.findDeviceById("6178027fe5a5c22ccc5deb5b"));
    }

    @Test
    public void updateDeviceState() {
        deviceDAO.updateDeviceStateById("617664f43f44352d14be7304", DeviceStateEnum.ABNORMAL);
    }

    @Test
    public void findAllDevicesId() {
        List<String> list = deviceDAO.findAllDevicesId();
        System.out.println(list.size());
    }

    @Test
    public void findDeviceBySensorType() {
        List<Sensor> sensors = deviceDAO.findDeviceBySensorType(SensorType.TEMPERATURE, new QueryPage(1,10));
        for (Sensor sensor : sensors) {
            System.out.println(sensor);
        }
    }

    @Test
    public void findDevicesByKey() {
        List<Sensor> sensors = deviceDAO.findDevicesByKey("温", new QueryPage(1,10));
        long num = deviceDAO.getNumberOfDevicesByKey("温");
        System.out.println(num);

        long sensorNum1 = deviceDAO.getNumOfSensor(SensorType.SMOKE);
        System.out.println("烟雾传感器总数：" + sensorNum1);

        long sensorNum2 = deviceDAO.getNumOfSensor(SensorType.TEMPERATURE);
        System.out.println("温度传感器总数：" + sensorNum2);

        long allDevices = deviceDAO.getNumOfAllDevices();
        System.out.println("传感器总数：" + allDevices);
        for (Sensor sensor : sensors) {
            System.out.println(sensor);
        }

    }

    @Test
    public void updateThreshold() {
        // deviceDAO.updateThresholdByDeviceState(DeviceStateEnum.NORMAL);
        deviceDAO.updateThresholdByDeviceState(DeviceStateEnum.ABNORMAL);
    }

    @Test
    public void getDate() {
        System.out.println(deviceDAO.getLatestHistoryByDidAndScreen("61780281e5a5c22ccc5deb70", ScreenEnum.SCREEN1));
        System.out.println(deviceDAO.getLatestHistoryByDidAndScreen("61780282e5a5c22ccc5deb7e", ScreenEnum.SCREEN1));
    }

}
