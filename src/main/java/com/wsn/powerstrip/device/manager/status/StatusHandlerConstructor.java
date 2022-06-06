package com.wsn.powerstrip.device.manager.status;

import com.wsn.powerstrip.common.pattern.responsibilityChainPattern.HandlerConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class StatusHandlerConstructor extends HandlerConstructor<AbstractStatusHandler> {

    public StatusHandlerConstructor(Map<String, AbstractStatusHandler> handlerMap) {
        super(handlerMap);
    }
}
