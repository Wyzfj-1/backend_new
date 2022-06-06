package com.wsn.powerstrip.device.manager.history.impl;

import com.wsn.powerstrip.common.util.TimeFormatTransUtils;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.exception.DeviceDAOException;
import com.wsn.powerstrip.device.manager.history.AbstractHistoryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: wangzilinn@gmail.com
 * @Modified wangzilinn@gmail.com,胡斐
 * 注意: 该handler负责处理所有redis相关的内容,其他handler不应操作redis 不要让其他handler使用redisTemplate
 * TODO：下面的很多逻辑都使用了3个if，这样写主要是便于调试，其实应该写成循环的
 *
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BufferHistoryHandler extends AbstractHistoryHandler {

    final private RedisTemplate<String, Object> redisTemplate;

    /**
     * 对传入的历史记录进行重采样
     * @param originalHistoryList 未经过重采样的历史记录
     * @param period 重采样之后的时间间隔(可以是null:不进行重采样, minute:重采样为1minute, hour: 重采样为1hour
     * @return 重采样的结果
     */
    @SuppressWarnings("all")
    public List<SensorHistory> resampleHistoryList(List<SensorHistory> originalHistoryList, TimeUnit period) {
        log.debug("正在进行重采样, 原数据长度为{}, 目标采样周期为{}", originalHistoryList.size(), period);
        Long distance = originalHistoryList.get(originalHistoryList.size() - 1).getDateTime() - originalHistoryList.get(0).getDateTime();
        if (period == null) {
            return originalHistoryList;
        }
        long plusSecond;
        switch (period) {
            case MINUTES:
                plusSecond = 60;
                break;
            case HOURS:
                plusSecond = 60 * 60;
                break;
            default:
                throw new DeviceDAOException(500, "不支持的重采样");
        }
        ArrayList<SensorHistory> result = new ArrayList<>();
        Long slotEndStamp = originalHistoryList.get(0).getDateTime() + plusSecond * 1000;
        int index = 0;
        while (index < originalHistoryList.size()){
            ArrayList<SensorHistory> historiesInSlot = new ArrayList<>();
            while (index < originalHistoryList.size() &&
                    originalHistoryList.get(index).getDateTime() < slotEndStamp) {
                SensorHistory deviceHistory = originalHistoryList.get(index);
                historiesInSlot.add(deviceHistory);
                index++;
            }
            SensorHistory averageHistory;
            if (historiesInSlot.size() != 0) {
                // TODO:这里只是取了中间的那个作为平均值,其实应该手动算这个slot内所有历史记录的平均的,但是这里用到了多态,不知道传入的
                // 是什么类型,所以写起来非常麻烦, 暂不实现
                averageHistory = historiesInSlot.get(historiesInSlot.size() / 2);
            }else {
                averageHistory = result.get(result.size() - 1);
            }
            result.add(averageHistory);
            slotEndStamp += plusSecond * 1000;
        }
        /*Instant slotEndInstant = TimeFormatTransUtils
                .timeStamp2Date(originalHistoryList.get(0).getDateTime())
                .toInstant().plusSeconds(plusSecond);
        int index = 0;
        while (index < originalHistoryList.size()) {
            ArrayList<SensorHistory> historiesInSlot = new ArrayList<>();
            while (index < originalHistoryList.size() && TimeFormatTransUtils
                    .timeStamp2Date(originalHistoryList.get(index).getDateTime())
                    .toInstant().isBefore(slotEndInstant)) {
                SensorHistory deviceHistory = originalHistoryList.get(index);
                historiesInSlot.add(deviceHistory);
                index++;
            }
            SensorHistory averageHistory;
            if (historiesInSlot.size() != 0) {
                // TODO:这里只是取了中间的那个作为平均值,其实应该手动算这个slot内所有历史记录的平均的,但是这里用到了多态,不知道传入的
                // 是什么类型,所以写起来非常麻烦, 暂不实现
                averageHistory = historiesInSlot.get(historiesInSlot.size() / 2);
            }else {
                averageHistory = result.get(result.size() - 1);
            }
            result.add(averageHistory);
            slotEndInstant = slotEndInstant.plusSeconds(60);
        }*/
        return result;
    }

    /**
     * 缓存策略为:每一个设备都有一个二级map,
     * id -> 1hour -> historyList (储存最近一小时数据, 不重采样, 长度约为600(设备上传周期约为10s一次))
     *       1day -> historyList (储存最近1天数据, 重采样为1分钟, 长度为1440 )
     *       1month -> historyList (储存最近1月数据, 重采样为1小时, 长度为720)
     * @param simpleDevice 设备实体
     * @param startDateTime 查询开始时间
     * @param endDateTime 查询结束时间
     * @return 查询结果
     */
    @Override
    @SuppressWarnings("all")
    public List<SensorHistory> getHistory(ScreenEnum screen, Sensor sensor, Long start, Long end){
        Long oneHourBefore = TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now().minusHours(1)),
            oneDayBefore = TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now().minusDays(1)),
            oneMonthBefore = TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now().minusMonths(1));
        String id = sensor.getId();
        log.debug("查询开始时间:{}", TimeFormatTransUtils.timestamp2localDateTime(start));
        log.debug("当前时间:{}", LocalDateTime.now(ZoneId.of("UTC")));
        if (!hasInited(screen, id, "hour")) {
            log.debug("初始化hour表");
            if (!pushHistoryListToRedis(screen, sensor, "hour", oneHourBefore, end)) {
                log.error("初始化失败,不存在过去的记录");
                return null;
            }
        }
        if (!hasInited(screen, id, "day")
                && start < oneHourBefore) {
            log.debug("初始化day表");
            if (!pushHistoryListToRedis(screen, sensor, "day", oneDayBefore, end)) {
                log.error("初始化day表失败");
                return null;
            }
        }
        if (!hasInited(screen, id, "month")
                && start < oneDayBefore) {
            log.debug("初始化month表");
            if(!pushHistoryListToRedis(screen, sensor, "month", oneMonthBefore, end)) {
                log.error("初始化month表失败");
                return null;
            }
        }
        if(start < oneMonthBefore){
            return null; //大于一个月的
            // DeviceServiceImpl 322-325 先返回一个null，后续看怎么处理
        }

        List<SensorHistory> result = null;
        /*if (startDateTime.isAfter(getEarliestDate(id, "hour"))) {*/
        if (start >= oneHourBefore) {
            // 使用hour list:
            Long latest = getLatestTimestamp(screen, id, "hour");
            if (latest == null || end > latest) {
                // 如果请求的结束时间比hour list中的还新, 则更新hour list 到请求的结束时间
                log.debug("redis hour中数据不全,请求最新的数据");
                pushHistoryListToRedis(screen, sensor, "hour", latest, end);
            }
            // 经过了上述操作, 缓存中一定已经存在了所有要查询的内容, 现在直接从缓存中取:
            result = getDeviceHistoryFromRedis(getKey(screen, sensor.getId(), "hour"), start, end);
            // 如果hour list 太长,清理太旧的历史记录
            removeTooOldHistory(screen, id, "hour");
        } /*else if (startDateTime.isAfter(getEarliestDate(id, "day"))) {*/
        else if (start < oneHourBefore && start >= oneDayBefore){
            // 使用 day list
            Long latest = getLatestTimestamp(screen, id, "day");
            if (latest == null || end > latest) {
                log.debug("redis day中数据不全,请求最新的数据");
                pushHistoryListToRedis(screen, sensor, "day", latest, end);
            }
            result = getDeviceHistoryFromRedis(getKey(screen, sensor.getId(), "day"), start, end);
            removeTooOldHistory(screen, id, "day");
        }/* else if (startDateTime.isAfter(getEarliestDate(id, "month"))) {*/
        else if (start < oneDayBefore && start >= oneMonthBefore) {
            Long latest = getLatestTimestamp(screen, id, "month");
            if (latest == null || end > latest) {
                log.debug("redis month中数据不全,请求最新的数据");
                pushHistoryListToRedis(screen, sensor, "month", latest, end);
            }
            result = getDeviceHistoryFromRedis(getKey(screen, sensor.getId(), "month"), start, end);
            removeTooOldHistory(screen, id, "month");
        }
        return result;
    }

    /**
     * 检查list是否过长, 如果太长则删除太老的历史记录
     * @param listName list 名
     */
    private void removeTooOldHistory(ScreenEnum screen, String id, String listName) {
        String redisKey = getKey(screen, id, listName);
        Long size = redisTemplate.opsForZSet().size(redisKey);
        assert size != null;
        long popCount;//需要进行pop操作的次数
        switch (listName) {
            case "hour":
                popCount = size - 7200; //hour list的长度最长为7200
                break;
            case "day":
                popCount = size - 20000;//day list的长度最长为20000
                break;
            case "month":
                popCount = size - 1000;//month list的长度最长为1000
                break;
            default:
                throw new DeviceDAOException(500, "参数无效");
        }
        if (popCount > 0) {
            redisTemplate.opsForZSet().removeRange(redisKey, 0, popCount);
        }
    }

//    /**
//     * 从下一个handler中取数据, 往redis的对应的list中push history
//     * @param simpleDevice 设备
//     * @param listName list 名
//     * @param endDateTime 更新该list到这个时间点
//     * @return true:添加成功 false:无添加内容
//     */
    /*private boolean pushHistoryListToRedis(
            Device simpleDevice, String listName, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.debug("从下一个handler中取数据,区间为{}到{}", startDateTime, endDateTime);
        List<DeviceHistory> historyList = nextHandler.getHistory(simpleDevice, startDateTime, endDateTime);
        if (historyList == null || historyList.size() == 0) {
            return false;
        }
        String redisKey = getKey(simpleDevice.getId(), listName);
        List<DeviceHistory> resampleHistoryList;
        switch (listName) {
            case "hour":
                resampleHistoryList = historyList;
                break;
            case "day":
                resampleHistoryList = resampleHistoryList(historyList, TimeUnit.MINUTES);
                break;
            case "month":
                resampleHistoryList = resampleHistoryList(historyList, TimeUnit.HOURS);
                break;
            default:
                throw new DeviceDAOException(500, "无法找到对应的list");
        }
        log.debug("为redis {} list {} 增加{}条记录", listName, simpleDevice.getId(), resampleHistoryList.size());
        redisTemplate.executePipelined(new SessionCallback<>() {
            //执行流水线
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //批量处理的内容
                resampleHistoryList.forEach(powerStripHistory -> operations.opsForZSet().add(redisKey,
                        powerStripHistory,
                        powerStripHistory.getDateTime().toInstant().toEpochMilli()));
                //注意这里一定要返回null，最终pipeline的执行结果，才会返回给最外层
                return null;
            }
        });
        return true;
    }*/
    private boolean pushHistoryListToRedis(
            ScreenEnum screen, Sensor sensor, String listName, Long start, Long end) {
        log.debug("从下一个handler中取数据,区间为{}到{}", start, end);
        List<SensorHistory> historyList = nextHandler.getHistory(screen, sensor, start, end);
        if (historyList == null || historyList.size() == 0) {
            return false;
        }
        String redisKey = getKey(screen, sensor.getId(), listName);
        List<SensorHistory> resampleHistoryList;
        switch (listName) {
            case "hour":
                resampleHistoryList = historyList;
                break;
            case "day":
                resampleHistoryList = resampleHistoryList(historyList, TimeUnit.MINUTES);
                break;
            case "month":
                resampleHistoryList = resampleHistoryList(historyList, TimeUnit.HOURS);
                break;
            default:
                throw new DeviceDAOException(500, "无法找到对应的list");
        }
        log.debug("为redis {} list {} 增加{}条记录", listName, sensor.getId(), resampleHistoryList.size());
        redisTemplate.executePipelined(new SessionCallback<>() {
            //执行流水线
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //批量处理的内容
                resampleHistoryList.forEach(sensorHistory -> operations.opsForZSet().add(redisKey,
                        sensorHistory,
                        sensorHistory.getDateTime()));
                //注意这里一定要返回null，最终pipeline的执行结果，才会返回给最外层
                return null;
            }
        });
        return true;
    }

    /**
     * @param id 设备id
     * @param period list 周期
     * @return redis对应的list的key
     */
    private String getKey(ScreenEnum screen, String id, String period) {
        return "history_" + screen + ":" + period + ":" + id;
    }


//    /**
//     * 仅从redis中的list中提取参数范围的历史记录
//     *
//     * @param redisKey key
//     * @param startDateTime 历史记录的开始时间
//     * @param endDateTime 历史记录的结束时间
//     * @return 历史记录列表
//     */
//    private List<DeviceHistory> getDeviceHistoryFromRedis(String redisKey, LocalDateTime startDateTime, LocalDateTime endDateTime) {
//        Long startKey = TimeFormatTransUtils.localDateTime2timeStamp(startDateTime);
//        Long endKey = TimeFormatTransUtils.localDateTime2timeStamp(endDateTime);
//        Set<Object> range = redisTemplate.opsForZSet().rangeByScore(redisKey, startKey, endKey);
//        if (range != null) {
//            ArrayList<Object> arrayList = new ArrayList<>(range);
//            return arrayList.stream().map(object -> (DeviceHistory) object).collect(Collectors.toList());
//        }
//        log.error("redis中理应存在全部数据，但是并没有");
//        return new ArrayList<>();
//    }
    private List<SensorHistory> getDeviceHistoryFromRedis(String redisKey, Long start, Long end) {
        Set<Object> range = redisTemplate.opsForZSet().rangeByScore(redisKey, start, end);
        if (range != null) {
            ArrayList<Object> arrayList = new ArrayList<>(range);
            return arrayList.stream().map(object -> (SensorHistory) object).collect(Collectors.toList());
        }
        log.error("redis中理应存在全部数据，但是并没有");
        return new ArrayList<>();
    }


    /**
     * @param id 设备id
     * @param listName list 名
     * @return 该list最古老数据的时间
     */
    /*private Long getEarliestDate(ScreenEnum screen, String id, String listName) {
        String redisKey = getKey(screen, id, listName);
        Set<Object> earliestSet = redisTemplate.opsForZSet().range(redisKey, 0, 0);
        assert earliestSet != null && earliestSet.size() == 1;
        SensorHistory earliestHistory = (SensorHistory) earliestSet.toArray()[0];
        return earliestHistory.getDateTime();
    }*/

    /**
     * @param id 设备id
     * @param listName list 名
     * @return 该list的最新数据的时间
     */
    /*private LocalDateTime getLatestDate(String id, String listName) {
        String redisKey = getKey(id, listName);
        Long size = redisTemplate.opsForZSet().zCard(redisKey);
        assert size != null;
        if (size == 0) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        Set<Object> earliestSet = redisTemplate.opsForZSet().range(redisKey, size - 1, size);
        assert earliestSet != null && earliestSet.size() == 1;
        DeviceHistory latestHistory = (DeviceHistory) earliestSet.toArray()[0];
        return TimeFormatTransUtils.date2LocalDateTime(latestHistory.getDateTime());
    }*/
    /**
     * @return 该list的最新数据的时间
     */
    private Long getLatestTimestamp(ScreenEnum screen, String id, String listName) {
        String redisKey = getKey(screen, id, listName);
        Long size = redisTemplate.opsForZSet().zCard(redisKey);
        assert size != null;
        if (size == 0) {
            return null;
        }
        Set<Object> earliestSet = redisTemplate.opsForZSet().range(redisKey, size - 1, size);
        assert earliestSet != null && earliestSet.size() == 1;
        SensorHistory latestHistory = (SensorHistory) earliestSet.toArray()[0];
        return latestHistory.getDateTime();
    }
    private Long getEarliestTimestamp(ScreenEnum screen, String id, String listName) {
        String redisKey = getKey(screen, id, listName);
        Set<Object> earliestSet = redisTemplate.opsForZSet().range(redisKey, 0, 0);
        assert earliestSet != null && earliestSet.size() == 1;
        SensorHistory earliestHistory = (SensorHistory) earliestSet.toArray()[0];
        return earliestHistory.getDateTime();
    }

    /**
     *
     * @param screen 大屏号
     * @param id 设备id
     * @param listName 表名/period
     * @return 该list是否存在
     */
    private boolean hasInited(ScreenEnum screen, String id, String listName) {
        boolean result = Objects.requireNonNullElse(redisTemplate.hasKey(getKey(screen, id, listName)), false);
        Long size = redisTemplate.opsForZSet().size(getKey(screen, id, listName));
        result = result && !(size == null || size == 0);
        if (result) {
            //当存在对应的list时,检查该list的最新记录是不是太遥远了,如果是,则删除该list
            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
            switch (listName) {
                case "hour":
                    if (getLatestTimestamp(screen, id, listName)
                            < TimeFormatTransUtils.localDateTime2timeStamp(now.minusHours(2))) {
                        redisTemplate.delete(getKey(screen, id, listName));
                        log.debug("已存在hour list, 但是其中的最新的历史记录还是2小时以前.故删除");
                        return false;
                    }
                case "day":
                    if (getLatestTimestamp(screen, id, listName)
                            < TimeFormatTransUtils.localDateTime2timeStamp(now.minusDays(2))) {
                        redisTemplate.delete(getKey(screen, id, listName));
                        log.debug("已存在day list, 但是其中的最新的历史记录还是2天以前.故删除");
                        return false;
                    }
                case "month":
                    if (getLatestTimestamp(screen, id, listName)
                            < TimeFormatTransUtils.localDateTime2timeStamp(now.minusMonths(2))) {
                        redisTemplate.delete(getKey(screen, id, listName));
                        log.debug("已存在month list, 但是其中的最新的历史记录还是2月以前.故删除");
                        return false;
                    }
            }
        }
        return result;
    }
}
