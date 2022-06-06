package com.wsn.powerstrip.communication.service.impl;

import com.wsn.powerstrip.communication.POJO.DTO.NotificationMessage;
import com.wsn.powerstrip.communication.service.SSEEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * UTF-8
 * Created by czy  Time : 2021/2/2 17:42
 * @Modified 2021-04-14 16:19 by Wang Zilin
 *
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SSEEmitterServiceImpl implements SSEEmitterService {

    /**
     * 当前连接数
     */
    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用map对象，便于根据organizationId和userId来获取对应的SseEmitter
     */
    // private final Map<Subscriber, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    // 为了获取组织id时不遍历，只能使用双层map，第一层key是organizationId， 第二层key是userId
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();
    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @return SseEmitter
     * @param role
     * @param userId
     */
    @Override
    public SseEmitter connect(String role, String userId) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(role, userId));
        sseEmitter.onError(errorCallBack(role, userId));
        sseEmitter.onTimeout(timeoutCallBack(role, userId));
        sseEmitterMap.compute(role, (id, userIdSseEmitterMap) -> {
            if (userIdSseEmitterMap == null) {
                userIdSseEmitterMap = new ConcurrentHashMap<>();
            }
            //相当于添加了一对key-value进role映射里
            userIdSseEmitterMap.put(userId, sseEmitter);
            return userIdSseEmitterMap;
        });
        // 数量+1
        count.getAndIncrement();
        return sseEmitter;
    }
    @Override
    public void sendMessage(String role, NotificationMessage notificationMessage) {
        ConcurrentHashMap<String, SseEmitter> userIdSseEmitterMap = sseEmitterMap.get(role);
        // 对于这个组织内所有的订阅用户发送消息：
        if (userIdSseEmitterMap != null) {
            userIdSseEmitterMap.forEachEntry(5, userIdSseEmitterEntry -> {
                try {
                    //实际就是sseEmitter
                    userIdSseEmitterEntry.getValue().send(SseEmitter.event()
                            .reconnectTime(1000)
                            .name("alarm")
                            .data(notificationMessage.getMessageMap(), MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    @Override
    public void removeSubscriber(String role, String userId) {
        sseEmitterMap.computeIfPresent(role, (id, userIdEmitterMap)->{
            userIdEmitterMap.remove(userId);
            if (userIdEmitterMap.isEmpty()) {
                userIdEmitterMap = null;
            }
            return userIdEmitterMap;
        });
        // 数量-1
        count.getAndDecrement();
    }

    /**
     * 获取当前连接数量
     */
    @Override
    public int getSubscribeCount() {
        return count.intValue();
    }


    /**
     * 这下面的函数在sse 完成，超时，出错时，都要删除对应的记录。
     */
    private Runnable completionCallBack(String role, String userId) {
        return () -> removeSubscriber(role, userId);
    }

    private Runnable timeoutCallBack(String role, String userId) {
        return () -> removeSubscriber(role, userId);
    }

    private Consumer<Throwable> errorCallBack(String role, String userId) {
        return throwable -> removeSubscriber(role, userId);
    }


}
