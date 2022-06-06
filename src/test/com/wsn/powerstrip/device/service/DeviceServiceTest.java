package com.wsn.powerstrip.device.service;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.communication.service.MessageService;
import com.wsn.powerstrip.communication.service.impl.MessageServiceImpl;
import com.wsn.powerstrip.device.DAO.DeviceDAO;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.POJO.DO.setting.Schedule;
import com.wsn.powerstrip.device.POJO.DTO.web.DeviceSummaryResponse;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 12/6/2020 9:01 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@ComponentScan(lazyInit = true)
@Slf4j
public class DeviceServiceTest {
    @Autowired
    DeviceService deviceService;

    @Autowired
    DeviceDAO deviceDAO;

    @Autowired
    MessageService messageService;

    @Test
    public void findAllDevice() {
        System.out.println(deviceService.findAllDevicesByDeviceState(DeviceStateEnum.NORMAL,new QueryPage(1,10)));
    }


    @Test
    public void test1(){
        messageService.sendPoliticsWarnSms(new String[]{"1","2","3","60"},"13223932668");
    }

    //@Test
    //public void fakePowerStripHistory() {
    //    deviceService.fakePowerStripHistory();
    //}

    //@Test
    /**
    public void addOrUpdateDevice() {
        //add
        PowerStrip powerStrip = new PowerStrip();
        powerStrip.setName("testPowerStrip");
        deviceService.addOrUpdateDevice(powerStrip);
        CircuitBreaker circuitBreaker = new CircuitBreaker();
        circuitBreaker.setName("test2CircuitBreaker");
        deviceService.addOrUpdateDevice(circuitBreaker);
        Sensor sensor = new Sensor();
        sensor.setName("testSensor");
        deviceService.addOrUpdateDevice(sensor);
    }

    @Test
    public void deleteDevice() {
        // 无此设备时测测试:
        deviceService.deleteDevice("602a5327805aea1398129f9e");
    }

    @Test
    public void findDevice() {
        //find sensor
        Sensor device3 = (Sensor) deviceService.findDevice("602a4ca49dd79176f14466fa");
        log.info(device3.toString());
    }

    @Test
    public void findDeviceLocationByOrganizationId() {
    }

    @Test
    public void updateAllDeviceOnlineTime() {
        deviceService.updateAllDeviceOnlineTime();
    }

    @Test
    public void attachSchedule() {
        deviceService.attachSchedule("5eed7f8e4f1c0960ae2d722e", DeviceTypeEnum.POWER_STRIP, "5eec414b04f7aa392228efa1");
        Schedule schedule = new Schedule();
        schedule.setName("test");
        deviceService.attachSchedule("5eed7f8e4f1c0960ae2d722e", DeviceTypeEnum.POWER_STRIP, schedule );
    }


    @Test
    public void findDevicesByOrganizationId() {
    }

    @Test
    public void findDeviceByBuildingIdAndOrganizationId() {
    }

    @Test
    public void findDeviceByRoomIdAndOrganizationId() {
    }

    @Test
    public void findDeviceByKeyAndOrganizationId() {
    }

    @Test
    public void getSummary() {
        DeviceSummaryResponse summary = deviceService.getSummary();
        log.info(summary.toString());
    }


    @Test
    public void getLatestStatus() {
        DeviceHistory latestStatus = deviceService.getLatestStatus("5eed7f8e4f1c0960ae2d722e", DeviceTypeEnum.POWER_STRIP);
        log.info(latestStatus.toString());
    }

    @Test
    public void getHistory() {
        List<? extends DeviceHistory> minute = deviceService.getHistory("5eed7f8e4f1c0960ae2d722e", DeviceTypeEnum.POWER_STRIP, "minute", 10);
        log.info(String.valueOf(minute.size()));
        minute.forEach(deviceHistory -> {
            System.out.println(deviceHistory.toString());
        });
    }

    @Test
    public void getForecast() {
    }

    @Test
    public void connectToAllIotPlatform() {
    }

    @Test
    public void saveIotPlatformHistoryToDB() {
    }

    @Test
    public void operation() {
        deviceService.operation("5eed7f8e4f1c0960ae2d722e", DeviceTypeEnum.POWER_STRIP, DeviceOperationEnum.OFF);
    }

    @Test
    public void switchAllPowerStripsBySettings() {
    }

    @Test
    public void checkAllPowerStripsAlarm() {
    }
    */
}
