package com.wsn.powerstrip.common.pattern.observerPattern;


/**
 * 通知者接口，也就是插座和电器的异常现象
 *
 * @Author: 夏星毅
 * @Modified wangzilinn@gmail.com
 */
public interface Observable<S extends Observable<S, O>, O extends Observer<S, O>> {
    //增加
    void attach(O observer);

    //删除
    void detach(O observer);

    //检查当前是否需要通知观察者，如果需要则进行通知
    void notifyAllObservers();
}
