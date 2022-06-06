package com.wsn.powerstrip.device.DAO;

import com.wsn.powerstrip.common.util.Counter;
import com.wsn.powerstrip.common.util.TimeFormatTransUtils;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.SensorType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class SensorHistoryDaoTest {

    @Resource
    private MongoTemplate mongoTemplateForDevice;

    @Resource
    private MongoTemplate mongoTemplateForDeviceHistory;

    @Test
    void contextLoads() {
        clearSensors();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 16; j++) {
                Counter sensorTest = mongoTemplateForDevice.findOne(
                        new Query(Criteria.where("collection").is("sensorTest")), Counter.class, "counter");
                assert sensorTest != null;
                Integer seq = sensorTest.getSeq();
                mongoTemplateForDevice.insert(
                        new Sensor("", "温度_" + ++seq,
                                DeviceStateEnum.NORMAL,
                                LocalDateTime.now(ZoneId.of("UTC")),
                                SensorType.TEMPERATURE,
                                LocalDateTime.now(ZoneId.of("UTC")).plusYears(1),
                                1, i, j, 50f, 300f),
                        "sensorTest");
                mongoTemplateForDevice.upsert(
                        new Query(Criteria.where("collection").is("sensorTest")),
                        new Update().inc("seq", 1),
                        "counter");
            }
        }
    }

    @Test
    void fakeSensorHistory1() {
        ExecutorService service = new ThreadPoolExecutor(5, 5, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        CountDownLatch latch = new CountDownLatch(128);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 16; j++) {
                int finalI = i;
                int finalJ = j;
                service.execute(() -> {
                    Sensor sensor = mongoTemplateForDevice.findOne(
                            new Query(Criteria.where("x").is(finalI).and("y").is(finalJ).and("position").is(1)), Sensor.class, "sensorTest");
                    System.out.println("sensor: " + sensor);
                    SensorHistory history = new SensorHistory();
                    assert sensor != null;
                    history.setDeviceId(sensor.getId());
                    history.setData(new Random().nextFloat() * 60);
                    history.setDateTime(TimeFormatTransUtils.localDateTime2timeStamp(LocalDateTime.now(ZoneId.of("UTC"))));
                    history.setX(finalI);
                    history.setY(finalJ);
                    mongoTemplateForDeviceHistory.insert(history, "screen" + sensor.getPosition());
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
    @Test
    void clearSensors(){
        mongoTemplateForDevice.remove(new Query(), "sensorTest");
    }

    @Test
    void clearHistories(){
        mongoTemplateForDeviceHistory.remove(new Query(), "screen1");
    }

}
