package com.wsn.powerstrip.device.service.impl;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.util.TimeFormatTransUtils;
import com.wsn.powerstrip.communication.publisher.impl.MessagePublisher;
import com.wsn.powerstrip.communication.publisher.impl.SSEPublisher;
import com.wsn.powerstrip.communication.publisher.impl.WeChatPublisher;
import com.wsn.powerstrip.device.DAO.AlarmDAO;
import com.wsn.powerstrip.device.DAO.DeviceDAO;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.POJO.DTO.web.DeviceSummaryResponse;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import com.wsn.powerstrip.device.manager.history.AbstractHistoryHandler;
import com.wsn.powerstrip.device.manager.history.HistoryHandlerConstructor;
import com.wsn.powerstrip.device.manager.history.impl.BufferHistoryHandler;
import com.wsn.powerstrip.device.manager.history.impl.DbHistoryHandler;
import com.wsn.powerstrip.device.manager.notification.alarm.OverThresholdAlarm;
import com.wsn.powerstrip.device.manager.notification.alarm.OverTimeAlarm;
import com.wsn.powerstrip.device.manager.status.StatusHandlerConstructor;
import com.wsn.powerstrip.device.service.DeviceService;
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
import java.util.concurrent.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceServiceImpl implements DeviceService {

    /**
     * 当前服务所使用的DAO
     */
    final private DeviceDAO deviceDAO;

    final private AlarmDAO alarmDAO;


    /**
     * 使用获得历史记录和最新状态的责任链:
     */
    final private HistoryHandlerConstructor historyHandlerConstructor;
    final private StatusHandlerConstructor statusHandlerConstructor;

    /**
     * 消息推送
     */
    final private WeChatPublisher weChatPublisher;
    final private SSEPublisher ssePublisher;
    final private MessagePublisher messagePublisher;

    private final List<OverThresholdAlarm> alarmList = new ArrayList<>();
    private final List<OverTimeAlarm> timeAlarms = new ArrayList<>();


    @Override
    public Sensor addOrUpdateDevice(Sensor sensor) {
        if (sensor.getId() == null) {
            if(sensor.getSensorType() == SensorType.TEMPERATURE) {
                sensor.setThreshold(55f);
            }
            if(sensor.getDeviceState() == null) {
                sensor.setDeviceState(DeviceStateEnum.NORMAL);
            }
            if(sensor.getCreateTime() == null) {
                sensor.setCreateTime(LocalDateTime.now(ZoneId.systemDefault()));
            }
            if(sensor.getCriticalUseTime() == 0) {
                sensor.setCriticalUseTime(100);
            }
            sensor.setScrappedTime(null);
            return deviceDAO.addDevice(sensor);
        } else {
            return deviceDAO.updateDevice(sensor);
        }
    }

    @Override
    public void deleteDevice(String id) {
        Sensor sensor = deviceDAO.deleteById(id);
        if (sensor == null) {
            log.error("删除设备时发现无此设备, id={}", id);
        }
    }


    @Override
    public void deleteDeviceList(List<String> idList) {
        if (idList == null) {
            log.error("删除设备列表为空");
        }
        for(String id : idList) {
            deleteDevice(id);
        }
    }

    @Override
    public Sensor findDevice(String id) {
        Sensor sensor = deviceDAO.findDeviceById(id);
        return sensor;
    }

    @Override
    public Response.Page<Sensor> findAllDevicesByDeviceState(DeviceStateEnum deviceState, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumOfDevices(deviceState);
        List<Sensor> sensors = deviceDAO.findAllDevices(deviceState, queryPage);
        return new Response.Page<>(sensors, queryPage, totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesBySensorType(SensorType sensorType, DeviceStateEnum deviceState, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesBySensorType(sensorType,deviceState);
        List<Sensor> sensors = deviceDAO.findDeviceBySensorType(sensorType,deviceState,queryPage);
        return new Response.Page<>(sensors, queryPage, totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesByKey(String keyword, DeviceStateEnum deviceState, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesByKey(keyword,deviceState);
        List<Sensor> sensors = deviceDAO.findDevicesByKey(keyword, deviceState, queryPage);
        return new Response.Page<>(sensors,queryPage,totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesBySensorType(SensorType sensorType, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumOfSensor(sensorType);
        List<Sensor> sensors = deviceDAO.findDeviceBySensorType(sensorType,queryPage);
        return new Response.Page<>(sensors, queryPage, totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesByKey(String keyword, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesByKey(keyword);
        List<Sensor> sensors = deviceDAO.findDevicesByKey(keyword, queryPage);
        return new Response.Page<>(sensors,queryPage,totalNumber);
    }

    /**
     * 根据开始时间或者结束时间或者设备的状态查询生产日期在给定时间之内的设备
     * @param startTime
     * @param deviceState
     * @param queryPage
     * @return
     */
    @Override
    public Response.Page<Sensor> findDevicesByStartTime(Date startTime, DeviceStateEnum deviceState, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesByStartTime(startTime, deviceState);
        List<Sensor> sensors = deviceDAO.findDevicesByStartTime(startTime, deviceState, queryPage);
        return new Response.Page<>(sensors,queryPage,totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesByStartTime(Date startTime, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesByStartTime(startTime);
        List<Sensor> sensors = deviceDAO.findDevicesByStartTime(startTime, queryPage);
        return new Response.Page<>(sensors,queryPage,totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesByStartTime(Date startTime, Date endTime, DeviceStateEnum deviceState, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesByStartTime(startTime, endTime, deviceState);
        List<Sensor> sensors = deviceDAO.findDevicesByStartTime(startTime, endTime, deviceState, queryPage);
        return new Response.Page<>(sensors,queryPage,totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesByStartTime(Date startTime, Date endTime, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfDevicesByStartTime(startTime, endTime);
        List<Sensor> sensors = deviceDAO.findDevicesByStartTime(startTime, endTime, queryPage);
        return new Response.Page<>(sensors,queryPage,totalNumber);
    }

    @Override
    public Response.Page<Sensor> findScrappedDevicesByStartTime(Date startTime, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfScrappedDevicesByStartTime(startTime);
        List<Sensor> sensors = deviceDAO.findScrappedDevicesByStartTime(startTime, queryPage);
        return new Response.Page<>(sensors, queryPage, totalNumber);
    }

    @Override
    public Response.Page<Sensor> findScrappedDevicesByStartTime(Date startTime, Date endTime, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfScrappedDevicesByStartTime(startTime, endTime);
        List<Sensor> sensors = deviceDAO.findScrappedDevicesByStartTime(startTime, endTime, queryPage);
        return new Response.Page<>(sensors, queryPage, totalNumber);
    }

    @Override
    public Response.Page<Sensor> findDevicesByPosition(int position, QueryPage queryPage) {
        long totalNumber = deviceDAO.getNumberOfAllDevicesByPosition(position);
        List<Sensor> sensors = deviceDAO.findDevicesPosition(position, queryPage);
        return new Response.Page<>(sensors, queryPage, totalNumber);
    }

    @Override
    public Sensor findDevicesByPosition(int position, int x, int y) {
        Sensor sensor = deviceDAO.findDevicesPosition(position, x, y);
        return sensor;
    }



    @Override
    public DeviceSummaryResponse getSummary() {
        long numOfDevices = deviceDAO.getNumOfAllDevices();
        long numOfNormalDevices = deviceDAO.getNumOfDevices(DeviceStateEnum.NORMAL);
        long numOfAbnormalDevices = deviceDAO.getNumOfDevices(DeviceStateEnum.ABNORMAL);
        long numOfScrappedDevices = deviceDAO.getNumOfDevices(DeviceStateEnum.SCRAPPED);
        long numOfSmokeSensor = deviceDAO.getNumOfSensor(SensorType.SMOKE);
        long numOfTempSensor = deviceDAO.getNumOfSensor(SensorType.TEMPERATURE);
        long numOfLeakageSensor = deviceDAO.getNumOfSensor(SensorType.LEAKAGE);
        long numOfElectricMeterSensor = deviceDAO.getNumOfSensor(SensorType.ELECTRICMETER);
        return DeviceSummaryResponse.builder()
                .numOfDevice(numOfDevices)
                .numOfNormalDevice(numOfNormalDevices)
                .numOfAbnormalDevices(numOfAbnormalDevices)
                .numOfScrappedDevice(numOfScrappedDevices)
                .numOfSmokeSensor(numOfSmokeSensor)
                .numOfTempSensor(numOfTempSensor)
                .numOfLeakageSensor(numOfLeakageSensor)
                .numOfElectricMeterSensor(numOfElectricMeterSensor)
                .build();
    }





    @Override
    public List<SensorHistory> getHistory(ScreenEnum screen, String id, String period, Integer times) {
        Sensor sensor = deviceDAO.findDeviceById(id);
        //TODO:先从缓存中找
        /*LocalDateTime startDateTime = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);*/
        Long endTimeStamp, startTimeStamp;
        startTimeStamp = endTimeStamp
                = TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now());
        /*WARNING:对于虚拟设备,修改开始查询日期到虚拟设备历史记录储存时间
        startDateTime = LocalDateTime.of(2020, 10, 20, startDateTime.getHour(), startDateTime.getMinute(), startDateTime.getSecond());
        currentTime = LocalDateTime.of(2020, 10, 20, currentTime.getHour(), currentTime.getMinute(), currentTime.getSecond());
        /*WARNING OVER

         */
        switch (period) {
            case "minute":
                startTimeStamp = TimeFormatTransUtils.localDateTime2timeStamp(
                        LocalDateTime.now().minusMinutes(times));
                break;
            case "hour":
                startTimeStamp = TimeFormatTransUtils.localDateTime2timeStamp(
                        LocalDateTime.now().minusHours(times));
                break;
            case "day":
                startTimeStamp = TimeFormatTransUtils.localDateTime2timeStamp(
                        LocalDateTime.now().minusDays(times));
                break;
        }
        AbstractHistoryHandler historyHandler = historyHandlerConstructor
                .construct(List.of(BufferHistoryHandler.class, DbHistoryHandler.class));
        return historyHandler.getHistory(screen, sensor, startTimeStamp, endTimeStamp);
    }



    private TimeUnit periodToTimeUnit(String period) {
        TimeUnit timeUnit;
        switch (period) {
            case "day":
                timeUnit = TimeUnit.DAYS;
                break;
            case "hour":
                timeUnit = TimeUnit.HOURS;
                break;
            case "minute":
            default:
                timeUnit = TimeUnit.MINUTES;
        }
        return timeUnit;
    }

    /**
     * 发送命令线程池
     */
    // private final ExecutorService sendCommandThreadPool = new ThreadPoolExecutor(10, 15, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10)); //发送命令并获得结果任务的线程池

    // private final Map<String, SendCommandTask> taskMap = new ConcurrentHashMap<>(); //正在运行或排队中的任务，key为设备id


    private boolean alarmInited = false;

    @Override
    public void checkAllDevicesAlarm() {
        if (!alarmInited) {
            List<Sensor> allDevicesList = new ArrayList<>();
            List<Sensor> allNormalDevices = deviceDAO.findAllDevices(DeviceStateEnum.NORMAL, null);
            List<Sensor> allAbnormalDevices = deviceDAO.findAllDevices(DeviceStateEnum.ABNORMAL, null);
            allDevicesList.addAll(allNormalDevices);
            allDevicesList.addAll(allAbnormalDevices);

            for (Sensor sensor : allDevicesList) {
                //为每一个传感器创建一个报警器
                OverThresholdAlarm overThresholdAlarm = OverThresholdAlarm.of(sensor,alarmDAO,deviceDAO);
                //对报警器附着一个警报发送器
                overThresholdAlarm.attach(weChatPublisher);
                //添加一个SSEPublisher
                overThresholdAlarm.attach(ssePublisher);
                overThresholdAlarm.attach(messagePublisher);
                alarmList.add(overThresholdAlarm);
            }

            //每一个设备创建一个时间阈值报警器
            for (Sensor sensor: allDevicesList){
                OverTimeAlarm overTimeAlarm = OverTimeAlarm.of(sensor,alarmDAO);
                overTimeAlarm.attach(weChatPublisher);
                overTimeAlarm.attach(ssePublisher);
                timeAlarms.add(overTimeAlarm);
            }

            alarmInited = true;
        }
        for (OverThresholdAlarm alarm : alarmList) {
            DeviceStateEnum deviceStateEnum = alarm.checkDevice();
            if (deviceStateEnum != null) {
                deviceDAO.updateDeviceStateById(alarm.getDeviceDbId(), deviceStateEnum);
            }
        }

        //时间阈值检查
        for (OverTimeAlarm alarm: timeAlarms){
            DeviceStateEnum deviceTimeState = alarm.checkDevice();
            if(deviceTimeState == DeviceStateEnum.SCRAPPED){
                log.info("设备" + alarm.getDeviceDbId() + "已报废");
                deviceDAO.updateDeviceStateById(alarm.getDeviceDbId(), deviceTimeState);
            }
        }

    }


//
//    public void fakePowerStripHistory() {
//        int[] s = {8, 10, 12, 14, 16, 18, 20, 22};
//        String[] ids = {"5f8d84004e0bc8750f532c1e", "5f8d876d2d57274c447ea724"};
//        for (String id : ids) {
//            for (int index = 0; index < s.length; index++) {
//                LocalDateTime endDateTime;
//                LocalDateTime startDateTime;
//                startDateTime = LocalDateTime.of(2020, 10, 20, s[index], 0, 0);
//                endDateTime = LocalDateTime.of(2020, 10, 20, s[index], 30, 0);
//                Date startDate = Date.from(startDateTime.toInstant(ZoneOffset.ofHours(8)));
//                Date endDate = Date.from(endDateTime.toInstant(ZoneOffset.ofHours(8)));
//                List<PowerStripHistory> histories = powerStripHistoryDAO.findDeviceHistoryByDbIdAndStartDateAndEndDate(id, startDate, endDate);
//                for (PowerStripHistory history : histories) {
//                    history.setBoardTemperature((float) (Math.random() * 6 + 35));
//                    // powerStripHistoryDAO.updatePowerStripHistory(history);
//                }
//            }
//        }
//        int[] s1 = {8, 10, 12, 14, 16, 18, 20, 22};
//        String[] ids1 = {"5f8d84004e0bc8750f532c1e", "5f8d876d2d57274c447ea724"};
//        for (String id : ids1) {
//            for (int index = 0; index < s.length; index++) {
//                LocalDateTime endDateTime;
//                LocalDateTime startDateTime;
//                startDateTime = LocalDateTime.of(2020, 10, 20, s1[index], 30, 0);
//                endDateTime = LocalDateTime.of(2020, 10, 20, s1[index] + 1, 0, 0);
//                Date startDate = Date.from(startDateTime.toInstant(ZoneOffset.ofHours(8)));
//                Date endDate = Date.from(endDateTime.toInstant(ZoneOffset.ofHours(8)));
//                List<PowerStripHistory> histories = powerStripHistoryDAO.findDeviceHistoryByDbIdAndStartDateAndEndDate(id, startDate, endDate);
//                for (PowerStripHistory history : histories) {
//                    history.setBoardTemperature((float) (Math.random() * 8 + 40));
//                    // powerStripHistoryDAO.updatePowerStripHistory(history);
//                }
//            }
//        }
//    }

    /***
     * 根据大屏显示实时数据
     * @param screenNum
     * @return
     */
    @Override
    public Float[][] getAllLatestHistoryByScreen(Integer screenNum) {
        ScreenEnum screen = ScreenEnum.select(screenNum);
        List<Sensor> list = deviceDAO.findDevicesPosition(screenNum,null);
        List<Float[]> data = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(list.size());
        int slices = 20;
        int part = list.size() / slices + 1;
        ExecutorService pool = new ThreadPoolExecutor(slices, slices, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        for(int i = 0; i < slices; i++){
            int finalI = i;
            pool.execute(() -> {
                for (int j = finalI * part; j < finalI * part + part && j < list.size(); j++){
                    SensorHistory temp = deviceDAO.getLatestHistoryByDidAndScreen(list.get(j).getId(), screen);
                    // TODO:too simple here...
                    if(temp == null){
                        log.error("no current data at " + ScreenEnum.SCREEN1 + ": " + list.get(j).getX() + ", " + list.get(j).getY());
                        latch.countDown();
                        continue;
                    }
                    data.add(new Float[]{Float.valueOf(temp.getX()), Float.valueOf(temp.getY()), temp.getData()});
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        pool.shutdown();
        return data.toArray(new Float[0][0]);
    }


    /**
     * 移除过老的历史
     * @param screen
     * @return
     */
    @Override
    public Integer removeTooOldHistory(ScreenEnum screen) {
        Long threshold = TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now().minusHours(6));
        deviceDAO.removeTooOldHistory(threshold, screen);
        return null;
    }
}
