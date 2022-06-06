package com.wsn.powerstrip.device.DAO;

import com.mongodb.DuplicateKeyException;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 2/1/2021 4:26 PM
 */
@Repository
@Slf4j
public class DeviceHistoryDAO {
    @Resource
    private MongoTemplate mongoTemplateForDeviceHistory;

    /**
     * 对一个设备新增一条历史记录
     * 每一个设备以id为名字新建一个collection
     *
     * @param id            设备的唯一id
     * @param deviceHistory 设备历史,需继承deviceHistory类进行实现
     */
    public void addDeviceHistory(String id, DeviceHistory deviceHistory) {
        // 无需判断Collection是否存在,如果不存在,mongodb会自动创建
        try {
            mongoTemplateForDeviceHistory.insert(deviceHistory, id);
        } catch (DuplicateKeyException e) {
            log.info("新建设备历史失败, 主键已存在");
        }
    }

    /**
     * 对一个设备新增一条历史记录
     * 每一个设备以id为名字新建一个collection
     *
     * @param id              设备的唯一id
     * @param deviceHistories 设备历史,需继承deviceHistory类进行实现
     */
    public void addDeviceHistory(String id, List<DeviceHistory> deviceHistories) {
        // 功能与上面的相同,只是添加一个列表
        try {
            mongoTemplateForDeviceHistory.insert(deviceHistories, id);
        } catch (DuplicateKeyException e) {
            log.info("新建设备历史失败, 主键已存在");
        }
    }

    /**
     * @param id                 设备唯一id
     * @param startDate          查询的开始事件
     * @param endDate            查询的结束时间(如果为null,则为查询到当前时间)
     * @param DeviceHistoryClass 设备类型,需
     * @return
     */
    public  List<? extends DeviceHistory> findDeviceHistoryByRange(String id, Date startDate, @Nullable Date endDate,
                                                Class<? extends DeviceHistory> DeviceHistoryClass) {
        Criteria criteria = Criteria.where("dateTime").gte(startDate);
        if (endDate != null) {
            criteria = criteria.lte(endDate);
        }
        return mongoTemplateForDeviceHistory.find(new Query(criteria), DeviceHistoryClass, id);
    }
//
//    public  List<? extends DeviceHistory> findDeviceHistoryByRange(String id, Date startDate, @Nullable Date endDate,
//                                                                   Class<? extends DeviceHistory> DeviceHistoryClass) {
//        Criteria criteria = Criteria.where("dateTime").gte(startDate);
//        if (endDate != null) {
//            criteria = criteria.lte(endDate);
//        }
//        return mongoTemplateForDeviceHistory.find(new Query(criteria), DeviceHistoryClass, id);
//    }

    public  List<? extends SensorHistory> findSensorHistoryByRange(ScreenEnum screen, String id, Long start, @Nullable Long end,
                                                                   Class<? extends SensorHistory> sensorHistoryClass) {
        Criteria criteria = Criteria.where("deviceId").is(id).and("dateTime").gte(start);
        if (end != null) {
            criteria = criteria.lte(end);
        }
        /**
         * 这里记得用Order，索引树查打乱了顺序，找半天原因......
         */
        return mongoTemplateForDeviceHistory.find(new Query(criteria).with(Sort.by(
                Sort.Order.asc("dateTime")
        )), sensorHistoryClass, screen.toString());
    }
}
