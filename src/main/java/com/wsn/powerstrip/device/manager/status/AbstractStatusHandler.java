package com.wsn.powerstrip.device.manager.status;

import com.wsn.powerstrip.common.pattern.responsibilityChainPattern.Handler;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.DeviceHistory;
import lombok.Data;
import org.springframework.lang.Nullable;


/**
 * @Author: wangzilinn@gmail.com
 * 该handler为完全责任链模式, 会调用链上的每个getStatus方法,在调用时, 比较当前handler和前一个handler的历史记录哪一个更新,并返回更新的那一个
 */
@Data
public abstract class AbstractStatusHandler implements Handler {

    protected AbstractStatusHandler nextHandler;

    @Override
    public void setNextHandler(Handler nextHandler){
        this.nextHandler = (AbstractStatusHandler) nextHandler;
    }

    /**
     * @param sensor 设备实体
     * @param deviceHistory 上一个handler获得的最新历史记录, 其中的内容与当前handler进行比较,返回更新的一个
     * @return 当前handler最新的历史记录
     */
    public abstract @Nullable DeviceHistory getStatus(Sensor sensor, DeviceHistory deviceHistory);

}
