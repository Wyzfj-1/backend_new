package com.wsn.powerstrip.common.pattern.responsibilityChainPattern;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class HandlerConstructor<T extends Handler> {

    final protected Map<String, T> handlerMap;

    public HandlerConstructor(Map<String, T> handlerMap) {
        TreeMap<String, T> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        treeMap.putAll(handlerMap);
        this.handlerMap = treeMap;
    }
    public T construct(List<Class<? extends T>> handlerOrder) {
        assert handlerOrder.size() >= 1;
        T firstHandler = handlerMap.get(handlerOrder.get(0).getSimpleName());
        T currentHandler = firstHandler;
        for (int i = 1; i < handlerOrder.size(); i++) {
            T nextHandler = handlerMap.get(handlerOrder.get(i).getSimpleName());
            currentHandler.setNextHandler(nextHandler);
            currentHandler = nextHandler;
        }
        return firstHandler;
    }
}
