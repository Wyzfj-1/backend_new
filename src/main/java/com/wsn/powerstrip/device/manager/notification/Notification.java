package com.wsn.powerstrip.device.manager.notification;

import com.wsn.powerstrip.common.pattern.observerPattern.Observable;
import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import com.wsn.powerstrip.communication.publisher.AbstractAsyncPublisher;
import lombok.Getter;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 通知实体, 设备的所有通知都发送到这里,内部使用观察者模式将通知信息发送给所有观察者
 * @Author: wangzilinn@gmail.com
 * @Date: 1/7/2021 11:39 AM
 */
public abstract class Notification implements Observable<Notification, AbstractAsyncPublisher> {
    @Getter
    private final CopyOnWriteArrayList<AbstractAsyncPublisher> observers = new CopyOnWriteArrayList<>();

    @Getter
    protected String sendToUserRole;

    public abstract NotificationMessage getAlarmMessage();

    /**
     * 为该通知附着观察者，如wechat，email等等，具体见AbstractPublisher的实现
     * @param observer 本通知的观察者
     */
    @Override
    public void attach(AbstractAsyncPublisher observer) {
        observers.add(observer);
    }

    @Override
    public void detach(AbstractAsyncPublisher observer) {
        observers.remove(observer);
    }

    /**
     * 通知所有观察者
     */
    @Override
    public void notifyAllObservers() {
        for (AbstractAsyncPublisher observer : observers) {
            observer.update(this);
        }
    }
}
