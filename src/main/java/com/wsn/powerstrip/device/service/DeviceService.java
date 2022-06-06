package com.wsn.powerstrip.device.service;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.POJO.DO.setting.Schedule;
import com.wsn.powerstrip.device.POJO.DTO.web.DeviceSummaryResponse;
import com.wsn.powerstrip.device.constants.DeviceStateEnum;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import com.wsn.powerstrip.device.constants.SensorType;

import java.util.Date;
import java.util.List;

public interface DeviceService {

    /**
     * 创建或更新设备
     * 如果设备的id字段为null,则为新建设备,否则为更新设备
     *
     * @param sensor 设备实体
     * @return 添加后的设备实体
     */
    Sensor addOrUpdateDevice(Sensor sensor);

    /**
     * 删除设备
     *
     * @param id             设备id
     */
    void deleteDevice(String id);


    /**
     * 批量删除设备
     * @param idList
     */
    void deleteDeviceList(List<String> idList);



    /**
     * 查找设备
     *
     * @param id             id
     * @return 查找到的设备
     */
    Sensor findDevice(String id);


    Response.Page<Sensor> findAllDevicesByDeviceState(DeviceStateEnum deviceState, QueryPage queryPage);

    Response.Page<Sensor> findDevicesBySensorType(SensorType sensorType, DeviceStateEnum deviceState, QueryPage queryPage);

    Response.Page<Sensor> findDevicesBySensorType(SensorType sensorType, QueryPage queryPage);

    /**
     * @param keyword        传感器名字中包含的内容
     * @param queryPage      分页参数
     * @return 参照关键字查询的插排结果
     */
    Response.Page<Sensor> findDevicesByKey(String keyword, DeviceStateEnum deviceState, QueryPage queryPage);

    Response.Page<Sensor> findDevicesByKey(String keyword, QueryPage queryPage);

    Response.Page<Sensor> findDevicesByStartTime(Date startTime, DeviceStateEnum deviceState, QueryPage queryPage);
    Response.Page<Sensor> findDevicesByStartTime(Date startTime, QueryPage queryPage);
    Response.Page<Sensor> findDevicesByStartTime(Date startTime, Date endTime, QueryPage queryPage);
    Response.Page<Sensor> findDevicesByStartTime(Date startTime, Date endTime, DeviceStateEnum deviceState, QueryPage queryPage);

    Response.Page<Sensor> findScrappedDevicesByStartTime(Date startTime, QueryPage queryPage);
    Response.Page<Sensor> findScrappedDevicesByStartTime(Date startTime, Date endTime, QueryPage queryPage);

    Response.Page<Sensor> findDevicesByPosition(int position, QueryPage queryPage);
    Sensor findDevicesByPosition(int position, int x, int y);

    DeviceSummaryResponse getSummary();


    /**
     * 获得设备历史
     *
     * @param id         id
     * @param screen 屏类型
     * @param period     周期
     * @param times      周期长度
     * @return 设备历史记录
     */
    List<SensorHistory> getHistory(ScreenEnum screen, String id, String period, Integer times);



    /**
     * 检查所有设备有没有触发报警,该方法被定时调用
     * 注意:设备的deviceStateEnum应该仅在这里更新
     *
     */
    void checkAllDevicesAlarm();

    Float[][] getAllLatestHistoryByScreen(Integer screenNum);

    Integer removeTooOldHistory(ScreenEnum screen);


}
