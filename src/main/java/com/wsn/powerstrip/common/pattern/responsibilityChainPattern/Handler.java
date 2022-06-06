package com.wsn.powerstrip.common.pattern.responsibilityChainPattern;

public interface Handler {
    void setNextHandler(Handler nextHandler);
}
