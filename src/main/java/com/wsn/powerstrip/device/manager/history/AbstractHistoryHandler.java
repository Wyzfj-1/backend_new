package com.wsn.powerstrip.device.manager.history;

import com.wsn.powerstrip.common.pattern.responsibilityChainPattern.Handler;
import com.wsn.powerstrip.device.POJO.DO.device.impl.Sensor;
import com.wsn.powerstrip.device.POJO.DO.deviceHistory.impl.SensorHistory;
import com.wsn.powerstrip.device.constants.ScreenEnum;
import lombok.Data;

import java.util.List;

/**
 * 定义了完全责任链模式,每个handler要么直接返回,要不移交给下一个
 */
@Data
public abstract class AbstractHistoryHandler implements Handler {

    protected AbstractHistoryHandler nextHandler;

    @Override
    public void setNextHandler(Handler nextHandler){
        this.nextHandler = (AbstractHistoryHandler) nextHandler;
    }

    // public abstract List<DeviceHistory> getHistory(Device simpleDevice, LocalDateTime startDateTime, LocalDateTime endDateTime);
    public abstract List<SensorHistory> getHistory(ScreenEnum screen, Sensor sensor, Long start, Long end);
}
