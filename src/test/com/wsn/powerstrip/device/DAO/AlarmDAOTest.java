package com.wsn.powerstrip.device.DAO;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.util.TimeFormatTransUtils;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.constants.AlarmTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * User: 珈
 * Time: 2021/10/20  14:48
 * Description:
 * Version:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class AlarmDAOTest {

    @Autowired
    AlarmDAO alarmDAO;

    /**
     * 根据alarm添加报警记录
     * 存入数据库的时间为UTC时间,比正常时间少8个小时,要不要改？
     */
    @Test
    public void addAlarm(){
        String deviceId = "714543ab49eb8ba47c8eaa3c";
        String deviceName = "温度07";
        AlarmTypeEnum alarmType = AlarmTypeEnum.OVER_THRESHOLD;
        //给alarm赋值
        //Alarm alarm = alarmDAO.findAlarmById("6171598d1da0c13bff6ed510");
        Alarm alarm = new Alarm();
        alarm.setDeviceName(deviceName);
        alarm.setDevicePosition(1);
        alarm.setDeviceId(deviceId);
        alarm.setAlarmType(alarmType);
        alarm.setAlarmFirstTime(LocalDateTime.now());
        alarm.setAlarmLatestTime(LocalDateTime.now());
        alarm.setHandleState(false);

        System.out.println("\n\n");
        System.out.println(alarmDAO.addAlarm(alarm));
    }



    // /**
    //  * 传感器首次报警添加报警记录
    //  */
    // @Test
    // public void insertAlarm(){
    //     System.out.println("\n\n");
    //     System.out.println(alarmDAO.insertAlarm("414543ab49eb8ba47c8eaa3c","温度04",
    //             AlarmTypeEnum.OVER_THRESHOLD));
    //     System.out.println("\n\n");
    //     // System.out.println(alarmDAO.);
    //
    // }

    /**
     * 删除已经处理的记录
     */
    @Test
    public void deleteHandled()
    {
        System.out.println("\n\n");
        System.out.println(alarmDAO.deleteHandled(true));
    }

    /**
     * 根据数据库中给定的id删除报警记录
     */
    @Test
    public void deleteById() {
        System.out.println("\n\n");
        System.out.println(alarmDAO.deleteById("6170de143d07580f07d2e70d"));
    }

    /**
     * 更新报警记录
     */
    @Test
    public void  updateAlarm(){
        // //更新最新报警时间
        // Alarm alarm = new Alarm();
        // alarm.setId("6171598d1da0c13bff6ed510");
        // alarm.setAlarmLatestTime(new Date());
        // System.out.println("\n\n");
        // System.out.println(alarmDAO.updateAlarm(alarm));

        //处理后更新状态
        // Alarm alarm = new Alarm();
        // alarm.setId("6171598d1da0c13bff6ed510");
        Alarm alarm = alarmDAO.findAlarmById("6171598d1da0c13bff6ed510");
        alarm.setHandleTime(LocalDateTime.now());
        alarm.setHandleState(true);
        System.out.println("\n\n");
        System.out.println(alarmDAO.updateAlarm(alarm));

    }

    // /**
    //  * 更新最新报警时间
    //  */
    // @Test
    // public void updateAlarmLatestTimeById()
    // {
    //     System.out.println("\n\n");
    //     System.out.println(alarmDAO.updateAlarmLatestTimeById("614543ab49eb8ba47c8eaa3c",new Date()));
    //     System.out.println("\n\n");
    //     System.out.println(alarmDAO.updateAlarmLatestTimeById("114543ab49eb8ba47c8eaa3c",new Date()));
    //
    // }
    //
    // /**
    //  * 处理后更新处理状态和处理时间
    //  */
    // @Test
    // public void updateAlarmStateById()
    // {
    //     System.out.println("\n\n");
    //     System.out.println(alarmDAO.updateAlarmStateById("6170c3de9a56d937de2c68a9",true));
    //     System.out.println("\n\n");
    //     System.out.println(alarmDAO.updateAlarmStateById("114543ab49eb8ba47c8eaa3c",true));
    //
    // }

    // /**
    //  * 查询所有记录
    //  */
    // @Test
    // public void findAllAlarm(){
    //     List<Alarm> alarms = alarmDAO.findAllAlarm();
    //     System.out.println("\n\n");
    //     for (Alarm alarm : alarms) {
    //         System.out.println(alarm);
    //     }
    // }

    /**
     * 根据id查询报警记录
     */
    @Test
    public void findAlarmById(){
        Alarm alarm = alarmDAO.findAlarmById("6171598d1da0c13bff6ed510");
        System.out.println("\n\n");
        System.out.println(alarm);
    }

    /**
     * 根据处理状态查询报警记录
     */
    @Test
    public void findAllAlarmByHandleType(){
        List<Alarm> alarms = alarmDAO.findAllAlarmByHandleState(true,new QueryPage(2,2));
        System.out.println("\n\n");
        for (Alarm alarm : alarms) {
            System.out.println(alarm);
        }
    }

    /**
     * 根据报警设备id查询报警记录
     */
    @Test
    public void findAllAlarmByDeviceId(){
        List<Alarm> alarms = alarmDAO.findAllAlarmByDeviceId("514543ab49eb8ba47c8eaa3c");
        System.out.println("\n\n");
        for (Alarm alarm : alarms) {
            System.out.println(alarm);
        }
    }

    // /**
    //  * 根据报警时间查询报警记录
    //  */
    // @Test
    // public void findAllAlarmByAlarmFirstTime(){
    //
    //     LocalDateTime start = LocalDateTime.of(2021, 10,21, 20, 0, 0);
    //     LocalDateTime end = LocalDateTime.of(2021, 10, 21, 22, 0, 0);
    //
    //     List<Alarm> alarms = alarmDAO.findAllAlarmByAlarmFirstTime(start,end);
    //     System.out.println("alarms:" + alarms);
    //     System.out.println("\n\n");
    //     for (Alarm alarm : alarms) {
    //         System.out.println("alarm:" + alarm);
    //     }
    // }

    /**
     * 根据报警时间查询报警记录总数
     */
    @Test
    public void getNumberOfAlarmByTime(){

        int year1 = 2021;
        int month1 = 9;
        LocalDateTime start = LocalDateTime.of(year1, month1+1,20, 20, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 10, 23, 22, 0, 0);

        System.out.println(alarmDAO.getNumberOfAlarmByTime(start,end));

    }

    /**
     * 统计报警记录总数
     */
    @Test
    public void getNumberOfAlarm(){
        System.out.println(alarmDAO.getNumberOfAlarm());
    }

    /**
     * 根据处理状态统计报警记录总数
     */
    @Test
    public void getNumberOfAlarmByHandleState(){
        System.out.println(alarmDAO.getNumberOfAlarmByHandleState(true));
    }

    /**
     * 根据位置统计正在报警的个数
     */
    @Test
    public void getNumberOfAlarmByPosition(){
        System.out.println(alarmDAO.getNumberOfAlarmByPosition(false,1));
    }

    //已处理报警信息列表

    /**
     * 根据报警时间查询已经处理的报警记录总数
     */
    @Test
    public void getNumberOfHandledAlarmByTime(){

        LocalDateTime start = LocalDateTime.of(2021, 10,20, 20, 0, 0);
        Date startTime = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime end = LocalDateTime.of(2021, 10, 23, 22, 0, 0);
        Date endTime = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTime(true,startTime,endTime));
        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTime(true,startTime,null));
        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTime(true,null,endTime));
        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTime(true,null,null));

    }

    /**
     * 报警时间查询
     */
    @Test
    public void findAlarmByTime(){
        int year1 = 2021;
        int month1 = 9;
        LocalDateTime start = LocalDateTime.of(year1, month1+1,20, 20, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 10, 23, 22, 0, 0);
        Date startTime = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("\n");
        System.out.println(alarmDAO.findAlarmByTime(true,startTime,endTime,new QueryPage(1,2)));
        System.out.println("\n\n");
        System.out.println(alarmDAO.findAlarmByTime(true,null,endTime,new QueryPage(1,2)));
        System.out.println("\n\n");
        System.out.println(alarmDAO.findAlarmByTime(true,startTime,null,new QueryPage(1,2)));
        System.out.println("\n\n");
        System.out.println(alarmDAO.findAlarmByTime(true,null,null,new QueryPage(1,2)));

    }


    /**
     * 根据报警时间查询已经处理的报警记录总数
     */
    @Test
    public void getNumberOfAlarmByTimeAndPos(){

        LocalDateTime start = LocalDateTime.of(2021, 10,20, 20, 0, 0);
        Date startTime = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime end = LocalDateTime.of(2021, 10, 23, 22, 0, 0);
        Date endTime = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTimeAndPos(true,1,startTime,endTime));
        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTimeAndPos(true,1,startTime,null));
        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTimeAndPos(true,1,null,endTime));
        System.out.println("\n"+alarmDAO.getNumberOfAlarmByTimeAndPos(true,1,null,null));

    }
    /**
     * 报警时间查询
     */
    @Test
    public void findAlarmByTimeAndPos(){
        int year1 = 2021;
        int month1 = 9;
        LocalDateTime start = LocalDateTime.of(year1, month1+1,20, 20, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 10, 23, 22, 0, 0);
        Date startTime = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("\n");
        System.out.println(alarmDAO.findAlarmByTimeAndPos(true,1,startTime,endTime,new QueryPage(1,2)));
        System.out.println("\n\n");
        System.out.println(alarmDAO.findAlarmByTimeAndPos(true,1,null,endTime,new QueryPage(1,2)));
        System.out.println("\n\n");
        System.out.println(alarmDAO.findAlarmByTimeAndPos(true,1,startTime,null,new QueryPage(1,2)));
        System.out.println("\n\n");
        System.out.println(alarmDAO.findAlarmByTimeAndPos(true,1,null,null,new QueryPage(1,2)));

    }


}
