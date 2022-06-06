package com.wsn.powerstrip.device.manager.schedule;

import com.wsn.powerstrip.common.util.TimeFormatTransUtils;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 10:19 PM 6/23/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleTask {

    final private DeviceService deviceService;

    @Deprecated
    @Resource
    final private MongoTemplate mongoTemplateForDeviceHistory;

    @Deprecated
    @Resource
    final private MongoTemplate mongoTemplateForDevice;

    /**
     * 每1min执行一次传感器数据的检测
     */
    @Scheduled(fixedRate = 60000)
    public void checkDevice() {
        log.info("开始检测所有传感器数据");
        deviceService.checkAllDevicesAlarm();
    }

    /**
     * 每1h移除过老的历史记录
     */
    @Scheduled(fixedRate = 3600000)
    public void removeTooOldHistory(){
        log.info("移除老数据");
        for (int i = 0; i < ScreenEnum.count(); i++) {
            deviceService.removeTooOldHistory(ScreenEnum.select(i));
        }
    }

    /**
     * 纯属瞎整，方便使用，后面必删
     */
    @Deprecated
    @Scheduled(fixedRate = 30000)
    void fakeHistories(){
        fakeSensorHistoryForScreen(1);
        fakeSensorHistoryForScreen(2);
        fakeSensorHistoryForScreen(3);
    }

    private void fakeSensorHistoryForScreen(Integer screenNum) {
        ScreenEnum screen = ScreenEnum.select(screenNum);
        // log.info("给screen1插入假数据: 128条，对应128个设备, at: " + LocalDateTime.now(ZoneId.of("UTC")));
        ExecutorService service = new ThreadPoolExecutor(5, 5, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        CountDownLatch latch = new CountDownLatch(screen.getRows() * screen.getCols());
        for (int i = 1; i <= screen.getRows(); i++) {
            for (int j = 1; j <= screen.getCols(); j++) {
                int finalI = i;
                int finalJ = j;
                service.execute(() -> {
                    Sensor sensor = mongoTemplateForDevice.findOne(
                            new Query(Criteria.where("x").is(finalI).and("y").is(finalJ).and("position").is(screenNum)), Sensor.class, "sensorTest");
                    SensorHistory history = new SensorHistory();
                    assert sensor != null;
                    history.setDeviceId(sensor.getId());
                    history.setData(new Random().nextFloat() * 60);
                    history.setDateTime(TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now()));
                    history.setX(finalI);
                    history.setY(finalJ);
                    mongoTemplateForDeviceHistory.insert(history, screen.toString());
                    latch.countDown();
                });
            }
        }
        try{
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        service.shutdown();
    }

}
