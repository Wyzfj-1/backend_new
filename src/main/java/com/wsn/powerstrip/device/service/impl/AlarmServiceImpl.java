package com.wsn.powerstrip.device.service.impl;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.device.DAO.AlarmDAO;
import com.wsn.powerstrip.device.DAO.DeviceDAO;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.POJO.DO.alarm.AlarmInfo;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: 珈
 * Time: 2021/10/21  18:06
 * Description:
 * Version:
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AlarmServiceImpl implements AlarmService {

    final private AlarmDAO alarmDAO;
    final private DeviceDAO deviceDao;

    /*
    * 统计所有报警记录的个数
    * */
    @Override
    public long getNumberOfAlarm() {
        return alarmDAO.getNumberOfAlarm();
    }

    /*
     * 根据报警状态查询报警记录个数
     * */
    @Override
    public long getNumberOfHandledAlarm(boolean handleState) {
        return alarmDAO.getNumberOfAlarmByHandleState(handleState);
    }

    /*
     * 根据位置统计正在报警的个数
     * */
    @Override
    public long getNumberOfAlarmByPosition(int position) {
        return alarmDAO.getNumberOfAlarmByPosition(false,position);
    }

    /*
     * 根据年份和月份统计当月报警总数,year：2021,month为1-12
     * */
    @Override
    public long getNumberOfAlarmByMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month,1, 0, 0, 0);
        LocalDateTime end;
        if (month != 12)
            end = LocalDateTime.of(year, month+1, 1, 0, 0, 0);
        else
            end = LocalDateTime.of(year+1, 1, 1, 0, 0, 0);
        return alarmDAO.getNumberOfAlarmByTime(start,end);
    }

    /*
     * 根据年份和月份统计当月已经处理的报警总数,year：2021,month为1-12
     * */
    @Override
    public long getNumberOfHandledAlarmByMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month,1, 0, 0, 0);
        Date startTime = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime end;
        if (month != 12)
            end = LocalDateTime.of(year, month+1, 1, 0, 0, 0);
        else
            end = LocalDateTime.of(year+1, 1, 1, 0, 0, 0);
        Date endTime = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        return alarmDAO.getNumberOfAlarmByTime(true,startTime,endTime);
    }

    /*
     *根据处理状态查询告警信息列表
     * */
    @Override
    public Response.Page<AlarmInfo> findAllAlarmByHandleState(Boolean handleState,QueryPage queryPage) {
        long totalNumber = alarmDAO.getNumberOfAlarmByHandleState(handleState);
        List<Alarm> alarms = alarmDAO.findAllAlarmByHandleState(handleState,queryPage);
        List<AlarmInfo> alarmInfoList = new ArrayList<>();
        for (Alarm alarm : alarms) {
            String alarmDeviceId =alarm.getDeviceId();
            Sensor sensor = deviceDao.findDeviceById(alarmDeviceId);
            AlarmInfo alarmInfo = new AlarmInfo(alarm);
            if (sensor != null){
                alarmInfo.setDeviceX(sensor.getX());
                alarmInfo.setDeviceY(sensor.getY());
            }
            alarmInfoList.add(alarmInfo);
        }
        return new Response.Page<>(alarmInfoList,queryPage,totalNumber);
    }

    // /*
    //  *已处理告警信息列表:未输入位置信息，根据时间查询
    //  * */
    // @Override
    // public Response.Page<Alarm> findHandledAlarmByTime(Date startTime, Date endTime, QueryPage queryPage) {
    //     long totalNumber = alarmDAO.getNumberOfAlarmByTime(true,startTime,endTime);
    //     List<Alarm> alarms = alarmDAO.findAlarmByTime(true,startTime,endTime,queryPage);
    //     return new Response.Page<>(alarms,queryPage,totalNumber);
    // }

    /*
     *已处理告警信息列表:未输入位置信息，根据时间查询
     * */
    @Override
    public Response.Page<AlarmInfo> findHandledAlarmByTime(Date startTime, Date endTime, QueryPage queryPage) {
        long totalNumber = alarmDAO.getNumberOfAlarmByTime(true,startTime,endTime);
        List<Alarm> alarms = alarmDAO.findAlarmByTime(true,startTime,endTime,queryPage);
        List<AlarmInfo> alarmInfoList = new ArrayList<>();
        for (Alarm alarm : alarms) {
            String alarmDeviceId =alarm.getDeviceId();
            Sensor sensor = deviceDao.findDeviceById(alarmDeviceId);
            AlarmInfo alarmInfo = new AlarmInfo(alarm);
            if (sensor != null){
                alarmInfo.setDeviceX(sensor.getX());
                alarmInfo.setDeviceY(sensor.getY());
            }
            alarmInfoList.add(alarmInfo);
        }


        return new Response.Page<>(alarmInfoList,queryPage,totalNumber);
    }

    /*
     *已处理告警信息列表:根据位置、时间查询
     * */
    @Override
    public Response.Page<AlarmInfo> findHandledAlarmByTimeAndPos(int position, Date startTime, Date endTime, QueryPage queryPage) {
        long totalNumber = alarmDAO.getNumberOfAlarmByTimeAndPos
                (true,position,startTime,endTime);
        List<Alarm> alarms = alarmDAO.findAlarmByTimeAndPos
                (true,position,startTime,endTime,queryPage);
        List<AlarmInfo> alarmInfoList = new ArrayList<>();
        for (Alarm alarm : alarms) {
            String alarmDeviceId =alarm.getDeviceId();
            Sensor sensor = deviceDao.findDeviceById(alarmDeviceId);
            AlarmInfo alarmInfo = new AlarmInfo(alarm);
            if (sensor != null){
                alarmInfo.setDeviceX(sensor.getX());
                alarmInfo.setDeviceY(sensor.getY());
            }
            alarmInfoList.add(alarmInfo);
        }
        return new Response.Page<>(alarmInfoList,queryPage,totalNumber);
    }








}
