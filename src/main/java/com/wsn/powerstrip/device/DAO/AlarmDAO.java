package com.wsn.powerstrip.device.DAO;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.DeleteResult;
import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.util.BeanUtil;
import com.wsn.powerstrip.device.POJO.DO.alarm.Alarm;
import com.wsn.powerstrip.device.constants.AlarmTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * User: 珈
 * Time: 2021/10/19  10:43
 * Description:
 * Version:
 */
@Repository
@Slf4j
@Validated
public class AlarmDAO {
    @Resource
    private MongoTemplate mongoTemplateForAlarm;

    private final String collectionName = "alarm_test";

    /**
     * 根据alarm添加报警记录
     * @param alarm
     * @return
     */
    public Alarm addAlarm(Alarm alarm){
        try {
            return mongoTemplateForAlarm.save(alarm,collectionName);
        } catch (DuplicateKeyException e) {
            log.info("添加报警记录失败，主键已存在");
            return null;
        }
    }

    // /**
    //  * 传感器首次报警添加报警记录
    //  * @param
    //  * @return
    //  */
    // public Alarm insertAlarm(String deviceId,String deviceName,AlarmTypeEnum alarmType){
    //
    //     //给alarm赋值
    //     Alarm alarm = new Alarm();
    //     alarm.setDeviceId(deviceId);
    //     alarm.setDeviceName(deviceName);
    //     alarm.setAlarmType(alarmType);
    //     alarm.setAlarmFirstTime(new Date());
    //     alarm.setAlarmLatestTime(new Date());
    //     alarm.setHandleState(false);
    //
    //     //判断数据库中是否存在
    //     Criteria criteria = Criteria.where("deviceId").is(deviceId)
    //             .and("alarmType").is(alarm.getAlarmType())
    //             .and("handleState").is(false);
    //
    //
    //     Alarm oldAlarm = mongoTemplateForAlarm.findOne(new Query(criteria), Alarm.class, collectionName);
    //     if (oldAlarm != null) {
    //         log.error("数据库已经存在未处理的该设备,插入失败");
    //         return null;
    //     }
    //     return mongoTemplateForAlarm.save(alarm,collectionName);
    // }

    /**
     * 根据数据库中给定的id删除报警记录
     * @param id
     * @return
     */
    public Alarm deleteById(String id) {
        return mongoTemplateForAlarm.findAndRemove(new Query(Criteria.where("_id").is(id)), Alarm.class,
                collectionName);
    }

    /**
     * 删除所有已经处理的记录
     * @param
     * @return
     */
    public DeleteResult deleteHandled(boolean handleState) {
        return mongoTemplateForAlarm.remove(new Query(Criteria.where("handleState").is(handleState)), collectionName);
    }

    /**
     * 更新报警记录
     * @param alarm
     * @return
     */
    public Alarm updateAlarm(Alarm alarm) {
        //save:存在主键则更新
        Alarm oldAlarm = mongoTemplateForAlarm.findOne(new Query(Criteria.where("_id").is(alarm.getId())),
                Alarm.class, collectionName);
        if (oldAlarm == null) {
            log.error("更新报警记录失败，无法找到对应id");
            return null;
        }
        //将修改后的非空字段复制到老的对象上面
        BeanUtil.copyNonNullProperties(alarm, oldAlarm);
        return (Alarm) mongoTemplateForAlarm.save(oldAlarm, collectionName);
    }


    // /**
    //  * 更新最新报警时间
    //  * @param deviceId,alarmLatestTime
    //  * @return
    //  */
    // public boolean updateAlarmLatestTimeById(String deviceId, Date alarmLatestTime)
    // {
    //     //根据设备id和处理状态查找
    //     Criteria criteria = Criteria.where("deviceId").is(deviceId)
    //             .and("handleState").is(false);
    //     Alarm alarm = mongoTemplateForAlarm.findOne(new Query(criteria), Alarm.class, collectionName);
    //     if (alarm == null) {
    //         log.error("更新最新报警时间失败");
    //         return false;
    //     }
    //
    //     return mongoTemplateForAlarm.updateFirst(
    //             new Query(criteria),
    //             new Update().set("alarmLatestTime", alarmLatestTime),
    //             collectionName
    //     ).wasAcknowledged();
    // }
    //
    //
    // /**
    //  * 处理后更新处理状态和处理时间
    //  * @param deviceId,handleState
    //  * @return
    //  */
    // public boolean updateAlarmStateById(String deviceId, Boolean handleState) {
    //     //根据设备id和处理状态查找
    //     Criteria criteria = Criteria.where("deviceId").is(deviceId)
    //             .and("handleState").is(false);
    //
    //     Alarm alarm = mongoTemplateForAlarm.findOne(new Query(criteria), Alarm.class, collectionName);
    //     if (alarm == null) {
    //         log.error("更新处理状态和处理时间失败");
    //         return false;
    //     }
    //
    //     return mongoTemplateForAlarm.upsert(
    //             new Query(criteria),
    //             new Update().set("handleState", handleState)
    //                     .set("handleTime", new Date()),
    //             collectionName
    //     ).wasAcknowledged();
    // }


    // /**
    //  * 查找所有报警记录
    //  * @param
    //  * @return
    //  */
    // public List<Alarm> findAllAlarm(){
    //     return mongoTemplateForAlarm.findAll(Alarm.class, collectionName);
    // }


    /**
     * 根据id查询报警记录
     * @param
     * @return
     */
    public Alarm findAlarmById(String id) {
        return mongoTemplateForAlarm.findOne(new Query(Criteria.where("_id").is(id)), Alarm.class, collectionName);
    }

    /**
     * 根据处理状态查询报警记录
     * @param
     * @return
     */
    public List<Alarm> findAllAlarmByHandleState(Boolean handleState,@Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("handleState").is(handleState));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForAlarm.find(query,Alarm.class, collectionName);
    }


    public List<Alarm> findAllAlarmByHandleState(Boolean handleState, AlarmTypeEnum alarmTypeEnum, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("handleState").is(handleState).and("alarmType").is(alarmTypeEnum));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForAlarm.find(query,Alarm.class, collectionName);
    }

    /**
     * 根据报警设备id查询报警记录
     * @param
     * @return
     */
    public List<Alarm> findAllAlarmByDeviceId(String deviceId) {
        Query query = new Query(Criteria.where("deviceId").is(deviceId));
        return mongoTemplateForAlarm.find(query, Alarm.class, collectionName);
    }

    public List<Alarm> findAlarmsByDeviceId(String deviceId, AlarmTypeEnum alarmTypeEnum, Boolean handleState) {
        return mongoTemplateForAlarm.find(new Query(Criteria.where("deviceId").is(deviceId).and("alarmType").is(alarmTypeEnum)
                                                .and("handleState").is(handleState)), Alarm.class, collectionName);
    }


    /**
     * 根据报警时间查询报警记录总数
     * @param
     * @return
     */
    public long getNumberOfAlarmByTime(LocalDateTime start, LocalDateTime end) {

        Date startTime = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());

        Date endTime = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        Query query = new Query(Criteria.where("alarmFirstTime").gte(startTime).lt(endTime));

        return mongoTemplateForAlarm.count(query, Alarm.class, collectionName);
    }


    /**
     * 统计报警记录总数
     * @param
     * @return
     */
    public Long getNumberOfAlarm() {
        Query query = new Query();
        return mongoTemplateForAlarm.count(query,Alarm.class,collectionName);
    }

    /**
     * 根据处理状态统计报警记录总数
     * @param
     * @return
     */
    public Long getNumberOfAlarmByHandleState(Boolean handleState) {
        Query query = new Query(Criteria.where("handleState").is(handleState));
        return mongoTemplateForAlarm.count(query,Alarm.class,collectionName);
    }

    /*
     * 根据位置统计正在报警的个数
     * */
    public long getNumberOfAlarmByPosition(boolean handleState, int position) {
        Query query = new Query(Criteria.where("handleState").is(handleState)
                .and("devicePosition").is(position));
        return mongoTemplateForAlarm.count(query,Alarm.class,collectionName);
    }



    //已处理告警信息列表


    /**
     * 报警时间查询:startTime和endTime可为null
     * @param
     * @return
     */
    public long getNumberOfAlarmByTime(Boolean handleState,Date startTime, Date endTime) {
        Query query;
        if (startTime != null){//开始时间不为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .andOperator(
                                Criteria.where("alarmFirstTime").lt(endTime),
                                Criteria.where("alarmFirstTime").gte(startTime)
                        ));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("alarmFirstTime").gte(startTime));
            }
        }
        else {//开始时间为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("alarmFirstTime").lt(endTime));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState));
            }
        }
        return mongoTemplateForAlarm.count(query, Alarm.class, collectionName);
    }

    /**
     * 报警时间查询:startTime和endTime可为null
     * @param
     * @return
     */
    public List<Alarm> findAlarmByTime(Boolean handleState,Date startTime, Date endTime,
                                       @Nullable QueryPage queryPage) {
        Query query;
        if (startTime != null){//开始时间不为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .andOperator(
                                Criteria.where("alarmFirstTime").lt(endTime),
                                Criteria.where("alarmFirstTime").gte(startTime)
                        ));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("alarmFirstTime").gte(startTime));
            }
        }
        else {//开始时间为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("alarmFirstTime").lt(endTime));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState));
            }
        }
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForAlarm.find(query, Alarm.class, collectionName);
    }

    /**
     * 报警时间、位置查询:startTime和endTime可为null
     * @param
     * @return
     */
    public long getNumberOfAlarmByTimeAndPos(Boolean handleState,int position,Date startTime, Date endTime) {
        Query query;
        if (startTime != null){//开始时间不为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position)
                        .andOperator(
                                Criteria.where("alarmFirstTime").lt(endTime),
                                Criteria.where("alarmFirstTime").gte(startTime)
                        ));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position)
                        .and("alarmFirstTime").gte(startTime));
            }
        }
        else {//开始时间为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position)
                        .and("alarmFirstTime").lt(endTime));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position));
            }
        }
        return mongoTemplateForAlarm.count(query, Alarm.class, collectionName);
    }

    /**
     * 报警时间、位置查询
     * @param
     * @return
     */
    public List<Alarm> findAlarmByTimeAndPos(Boolean handleState,int position,Date startTime, Date endTime,
                                             @Nullable QueryPage queryPage) {
        Query query;
        if (startTime != null){//开始时间不为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position)
                        .andOperator(
                                Criteria.where("alarmFirstTime").lt(endTime),
                                Criteria.where("alarmFirstTime").gte(startTime)
                        ));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position)
                        .and("alarmFirstTime").gte(startTime));
            }
        }
        else {//开始时间为空
            if (endTime != null){//结束时间不为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position)
                        .and("alarmFirstTime").lt(endTime));
            }
            else {//结束时间为空
                query = new Query(Criteria.where("handleState").is(handleState)
                        .and("devicePosition").is(position));
            }
        }
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForAlarm.find(query, Alarm.class, collectionName);
    }




}
