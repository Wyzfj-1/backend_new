package com.wsn.powerstrip.device.POJO.DO.deviceHistory;

import com.wsn.powerstrip.device.constants.SensorType;

import java.util.Date;

/**
 * 设备历史记录的实体,注意:设备历史记录中应不存在设备所属云平台相关信息,事实上,在device包中的所有的内容都应该看不到设备的云平台的内容
 */
public interface DeviceHistory {

    /**
     * @return 设备在mongodb的唯一id
     */
    String getDbId();

    Date getDateTime();


}
