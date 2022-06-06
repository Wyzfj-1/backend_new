package com.wsn.powerstrip.device.DAO;

import com.mongodb.DuplicateKeyException;
import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.util.BeanUtil;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 3:51 PM 6/16/2020
 * @Modified 夏星毅
 * TODO：按理说deviceDAO应该做一个内存缓存，例如所有设备实体应该都存在内存中
 */
@Repository
@Slf4j
@Validated
public class DeviceDAO {

    @Resource
    @Qualifier("mongoTemplateForDevice")
    private MongoTemplate mongoTemplateForDevice;

    @Resource
    @Qualifier("mongoTemplateForDeviceHistory")
    private MongoTemplate mongoTemplateForDeviceHistory;

    private final String collectionName = "sensorTest";

    public Sensor addDevice(Sensor sensor) {
        try {
            return mongoTemplateForDevice.save(sensor, collectionName);
        } catch (DuplicateKeyException e) {
            log.info("新增设备失败，主键已存在");
            return null;
        }
    }

    /**
     * 根据数据库中给定的id删除设备
     * @param id
     * @return
     */
    public Sensor deleteById(String id) {
        return mongoTemplateForDevice.findAndRemove(new Query(Criteria.where("_id").is(id)), Sensor.class,
                collectionName);
    }


    public Sensor updateDevice(Sensor sensor) {
        //save:存在主键则更新
        Sensor oldDevice = mongoTemplateForDevice.findOne(new Query(Criteria.where("_id").is(sensor.getId())),
               Sensor.class, collectionName);
        if (oldDevice == null) {
            log.error("更新设备时失败，无法找到对应id");
            return null;
        }
        //将修改后的非空字段复制到老的对象上面
        BeanUtil.copyNonNullProperties(sensor, oldDevice);
        return (Sensor) mongoTemplateForDevice.save(oldDevice, collectionName);
    }

    public void updateThresholdByDeviceState(DeviceStateEnum deviceStateEnum) {
        List<Sensor> sensors = this.findAllDevices(deviceStateEnum, null);
        for(Sensor sensor : sensors) {
            sensor.setThreshold(59.5f);
            updateDevice(sensor);
        }
    }


    // 关于设备的状态修改、阈值等都在checkAllDevicesAlarm()中
    public boolean updateDeviceStateById(String id, DeviceStateEnum deviceState) {
        return mongoTemplateForDevice.upsert(
                new Query(Criteria.where("_id").is(id)),
                new Update().set("deviceState", deviceState)
                        .set("updateTime", new Date()),
                collectionName
        ).wasAcknowledged();
    }


    public Sensor findDeviceById(String id) {
        return mongoTemplateForDevice.findOne(new Query(Criteria.where("_id").is(id)), Sensor.class, collectionName);
    }

    public List<String> findAllDevicesId(){
        Query query = new Query();
        query.fields().include("_id");
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName).stream().map(Sensor::getId).collect(Collectors.toList());
    }


    /**
     * 根据设备类型以及设备状态查找相应的设备
     * @param sensorType
     * @param deviceState
     * @return
     */
    public List<Sensor> findDeviceBySensorType(SensorType sensorType, DeviceStateEnum deviceState, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("sensorType").is(sensorType)
        .and("deviceState").is(deviceState));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);

    }

    public List<Sensor> findDeviceBySensorType(SensorType sensorType, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("sensorType").is(sensorType));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);

    }

    public long getNumberOfDevicesBySensorType(SensorType sensorType, DeviceStateEnum deviceState) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("sensorType").is(sensorType)
        .and("deviceState").is(deviceState)),Sensor.class,collectionName);
    }


    public List<Sensor> findDevicesByKey(String keyword, DeviceStateEnum deviceState,
                                         @Nullable QueryPage queryPage) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("name").regex(keyword).and("deviceState").is(deviceState));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);
    }

    public long getNumberOfDevicesByKey(String keyword, DeviceStateEnum deviceState) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("name").regex(keyword).and("deviceState").is(deviceState));
        return mongoTemplateForDevice.count(query, Sensor.class, collectionName);
    }

    /**
     *  在查询设备历史的时候需要将所有的数据都查出来
     * @param keyword
     * @param queryPage
     * @return
     */
    public List<Sensor> findDevicesByKey(String keyword,
                                         @Nullable QueryPage queryPage) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("name").regex(keyword));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);
    }

    public long getNumberOfDevicesByKey(String keyword) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("name").regex(keyword));
        return mongoTemplateForDevice.count(query, Sensor.class, collectionName);
    }

    /**
     *  查询在某一时间段内安装的传感器设备
     * @param startTime
     * @return
     */
    public List<Sensor> findDevicesByStartTime(Date startTime, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("creatTime").gte(startTime));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query,Sensor.class,collectionName);
    }

    public List<Sensor> findDevicesByStartTime(Date startTime, DeviceStateEnum deviceState, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("creatTime").gte(startTime)
                .and("deviceState").is(deviceState));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query,Sensor.class,collectionName);
    }

    public List<Sensor> findDevicesByStartTime(Date startTime, Date endTime, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("creatTime").gte(startTime).lte(endTime));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query,Sensor.class,collectionName);
    }

    public List<Sensor> findDevicesByStartTime(Date startTime, Date endTime, DeviceStateEnum deviceState, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("creatTime").gte(startTime).lte(endTime)
                .and("deviceState").is(deviceState));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query,Sensor.class,collectionName);
    }

    public long getNumberOfDevicesByStartTime(Date startTime) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("creatTime").gte(startTime)),Sensor.class,collectionName);
    }

    public long getNumberOfDevicesByStartTime(Date startTime, DeviceStateEnum deviceState) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("creatTime").gte(startTime)
                .and("deviceState").is(deviceState)), Sensor.class, collectionName);
    }

    public long getNumberOfDevicesByStartTime(Date startTime, Date endTime) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("creatTime").gte(startTime)
                .lte(endTime)), Sensor.class, collectionName);
    }

    public long getNumberOfDevicesByStartTime(Date startTime, Date endTime, DeviceStateEnum deviceState) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("creatTime").gte(startTime).lte(endTime)
                .and("deviceState").is(deviceState)), Sensor.class, collectionName);
    }
    /**
     * 查询在某一段时间内报废的设备
     * @param startTime
     * @return
     */
    public List<Sensor> findScrappedDevicesByStartTime(Date startTime, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("scrappedTime").gte(startTime).and("deviceState").is(DeviceStateEnum.SCRAPPED));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);
    }

    public List<Sensor> findScrappedDevicesByStartTime(Date startTime, Date endTime, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("scrappedTime").gte(startTime).lte(endTime).and("deviceState").is(DeviceStateEnum.SCRAPPED));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);
    }

    public long getNumberOfScrappedDevicesByStartTime(Date startTime) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("scrappedTime").gte(startTime)
                .and("deviceState").is(DeviceStateEnum.SCRAPPED)), Sensor.class, collectionName);
    }

    public long getNumberOfScrappedDevicesByStartTime(Date startTime, Date endTime) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("scrappedTime").gte(startTime).lte(endTime)
                .and("deviceState").is(DeviceStateEnum.SCRAPPED)), Sensor.class, collectionName);
    }

    /**
     *  返回所有的传感器设备总数
     * @return
     */
    public long getNumOfAllDevices() {
        Query query = new Query();
        query.fields().include("_id");
        return mongoTemplateForDevice.count(query, Sensor.class, collectionName);
    }

    public List<Sensor> findAllDevices(DeviceStateEnum deviceState, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("deviceState").is(deviceState));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query, Sensor.class, collectionName);
    }


    public long getNumOfDevices(DeviceStateEnum deviceState) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("deviceState").is(deviceState)), Sensor.class, collectionName);
    }

    public long getNumOfSensor(SensorType sensorType) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("sensorType").is(sensorType)), Sensor.class, collectionName);
    }

    public List<Sensor> findDevicesPosition(int position, @Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("position").is(position));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForDevice.find(query,Sensor.class,collectionName);
    }

    public long getNumberOfAllDevicesByPosition(int position) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("position").is(position)),Sensor.class,collectionName);
    }

    public Sensor findDevicesPosition(int position, int x, int y) {
        Query query = new Query(Criteria.where("position").is(position).and("x").is(x).and("y").is(y));
        return mongoTemplateForDevice.findOne(query,Sensor.class,collectionName);
    }


    /***
     * ------------------------------------------------------
     * ----------------------理政中心-实时展示------------------
     */
    public long getNumberOfDevicesByPosition(int position) {
        return mongoTemplateForDevice.count(new Query(Criteria.where("position").is(position)
                .and("deviceState").is(DeviceStateEnum.NORMAL)),Sensor.class,collectionName);
    }

    /**
     * 查询设备最新一条历史
     * @param dId
     * @param screen
     * @return
     */
    public SensorHistory getLatestHistoryByDidAndScreen(String dId, ScreenEnum screen){
        List<SensorHistory> res;
        Query query = new Query();
        Criteria criteria = Criteria.where("deviceId").is(dId);
        query.addCriteria(criteria);
        query.limit(1);
        query.with(Sort.by(
                Sort.Order.desc("dateTime")
        ));
        return (res = mongoTemplateForDeviceHistory.find(query, SensorHistory.class, screen.toString()))
                .size() == 0 ? null : res.get(0);
    }

    public long removeTooOldHistory(Long threshold, ScreenEnum screen){
        return mongoTemplateForDeviceHistory.remove(new Query(Criteria.where("dateTime").lt(threshold)), SensorHistory.class,
                screen.toString()).getDeletedCount();
    }

}
