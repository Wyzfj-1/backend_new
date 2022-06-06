package com.wsn.powerstrip.device.manager.history;

import com.wsn.powerstrip.common.pattern.responsibilityChainPattern.HandlerConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class HistoryHandlerConstructor extends HandlerConstructor<AbstractHistoryHandler> {

    public HistoryHandlerConstructor(Map<String, AbstractHistoryHandler> handlerMap) {
        super(handlerMap);
    }
}
