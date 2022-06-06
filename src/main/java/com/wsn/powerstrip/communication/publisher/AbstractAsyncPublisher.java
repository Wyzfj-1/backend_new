package com.wsn.powerstrip.communication.publisher;

import com.wsn.powerstrip.common.pattern.observerPattern.Observer;
import com.wsn.powerstrip.device.manager.notification.Notification;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 1/3/2021 4:14 PM
 * @Modified 2021-04-11 18:09 by Wang Zilin
 */
@Slf4j
public abstract class AbstractAsyncPublisher implements Observer<Notification, AbstractAsyncPublisher> {
    // 这个字段用于实现消息的异步发送
    public LinkedBlockingQueue<Notification> notificationsFlow = new LinkedBlockingQueue<>();
    // 该异步推送器是否已经开始运行的指示变量
    public boolean asyncPushStarted = false;

    @Override
    synchronized public void update(Notification subject) {
        notificationsFlow.offer(subject);
        if (notificationsFlow.size() > 1000) {
            log.warn("已经有{}条通知未被发送, 最新的未发送通知为{}", notificationsFlow.size(), subject);
        }
        if (!asyncPushStarted) {
            startAsyncUpdate();
            asyncPushStarted = true;
        }
    }

    /**
     * 懒汉模式启动异步推送消息
     * 该方法中执行具体的推送逻辑
     */
    private void startAsyncUpdate(){
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Notification notification = notificationsFlow.take();
                    asyncUpdateInternal(notification);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 执行推送消息的具体逻辑
     * @param notification 要推送的消息实体
     */
    protected abstract void asyncUpdateInternal(Notification notification);
}
