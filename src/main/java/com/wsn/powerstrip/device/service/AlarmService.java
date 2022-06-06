package com.wsn.powerstrip.device.service;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.POJO.DO.alarm.AlarmInfo;

import java.util.Date;

/**
 * User: 珈
 * Time: 2021/10/21  18:06
 * Description:
 * Version:
 */
public interface AlarmService {

    /*
    * 统计所有报警记录的个数
    * */
    long getNumberOfAlarm();

    /*
     * 根据报警状态查询报警记录个数
     * */
    long getNumberOfHandledAlarm(boolean handleState);


    /*
     * 根据位置统计正在报警的个数
     * */
    long getNumberOfAlarmByPosition(int position);

    /*
     * 根据年份和月份统计当月报警总数,year：2021,month为1-12
     * */
    long getNumberOfAlarmByMonth(int year, int month);

    /*
     * 根据年份和月份统计当月已经处理的报警总数,year：2021,month为1-12
     * */
    long getNumberOfHandledAlarmByMonth(int year, int month);

    /*
     *根据处理状态查询告警信息列表
     * */
    Response.Page<AlarmInfo> findAllAlarmByHandleState(Boolean handleState, QueryPage queryPage);

    /*
     *已处理告警信息列表:根据时间查询
     * */
    Response.Page<AlarmInfo> findHandledAlarmByTime(Date startTime, Date endTime, QueryPage queryPage);


    /*
     *已处理告警信息列表:根据位置、时间查询
     * */
    Response.Page<AlarmInfo> findHandledAlarmByTimeAndPos(int position, Date startTime, Date endTime, QueryPage queryPage);
}
