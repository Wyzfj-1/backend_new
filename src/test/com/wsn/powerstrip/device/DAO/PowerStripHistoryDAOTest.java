package com.wsn.powerstrip.device.DAO;

import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 2/1/2021 4:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
public class PowerStripHistoryDAOTest {
    @Autowired
    DeviceHistoryDAO deviceHistoryDAO;

    @Autowired
    DeviceDAO deviceDAO;

    @Test
    public void addDeviceHistory() {

    }

    @Test
    public void testAddDeviceHistory() {
    }

    @Test
    public void findDeviceHistoryByRange() {
    }

}
