package com.wsn.powerstrip.common.limiter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 一个固定流量的限流器
 *
 * @Author: hufei
 * @Modified wangzilinn@gmail.com
 * @Date: 4/7/2021 5:20 PM
 */
@Slf4j
public class FixedRateLimiter {
    private final int limitTimes;
    private final long limitMinutes;
    private final RateLimiterLoopQueue<LocalDateTime> limiterLoopQueue;
    // 是否已经存在超过阈值的请求
    private boolean hasExcessRequest = false;

    /**
     * 允许在minutes分钟内产生times次流量
     *
     * @param limitTimes 允许多少次
     * @param minutes    在多少分钟内
     */
    public FixedRateLimiter(int limitTimes, long minutes) {
        this.limitTimes = limitTimes;
        this.limitMinutes = minutes;
        limiterLoopQueue = new RateLimiterLoopQueue<>(limitTimes + 10);
    }


    /**
     * 判断是否有请求正在被限流
     * @return true:当前请求可以执行后续的操作, false:已存在被限流请求正在sleep, 不要进行后续的操作
     * @throws InterruptedException
     */
    public boolean checkAndHoldFirstRequest() throws InterruptedException {
        // 当前请求是否需要等待
        boolean shouldWait = false;
        long waitTime = 0;
        synchronized (this) {
            if (!hasExcessRequest) {
            // 还不存在超过阈值的请求, 判断该请求是否能通过
                limiterLoopQueue.offer(LocalDateTime.now());
                // 从队列中剔除过期的请求
                while (!limiterLoopQueue.isEmpty() && LocalDateTime.now().minusMinutes(limitMinutes).isAfter(limiterLoopQueue.getFrontElement())) {
                    //TODO:logn
                    limiterLoopQueue.poll();
                }
                if (limiterLoopQueue.getSize() >= limitTimes){
                // 当前请求超过阈值:
                    Duration duration = Duration.between(LocalDateTime.now().minusMinutes(1), limiterLoopQueue.getFrontElement());
                    waitTime = duration.toMillis() + 1000;
                    log.debug("访问OC过于频繁，需等待{}秒", waitTime / 1000.0);
                    shouldWait = true;
                    hasExcessRequest = true;
                }
            }else {
                // 已存在超过阈值的请求正在sleep,则当前请求直接返回false
                return false;
            }
        }
        if (shouldWait) {
            Thread.sleep(waitTime);
            // 如果到这里,说明当前请求是第一个超阈值的请求, 且睡够了之后停止限流
            hasExcessRequest = false;
        }
        log.debug("当前60s内请求数：" + limiterLoopQueue.getSize());
        log.debug("当前60s内最初请求时间：" + limiterLoopQueue.getFrontElement());
        return true;
    }

    /**
     * 一个用于限制流量的消息队列
     * @author hufei
     * @param <E> 循环队列的内容和
     */
    private static class RateLimiterLoopQueue<E extends Comparable<?>> {
        private final E[] loopQueue;
        private int size;
        private int front;
        private int tail;

        public RateLimiterLoopQueue(int capacity) {
            this.loopQueue = (E[]) new Comparable[capacity + 1];
            this.size = 0;
            this.front = 0;
            this.tail = 0;
        }

        public RateLimiterLoopQueue() {
            this(100);
        }

        public int getSize() {
            return this.size;
        }

        public boolean isEmpty() {
            return front == tail;
        }

        public boolean isFull() {
            return front == (tail + 1) % loopQueue.length;
        }

        public void offer(E element) {
            if (isFull()) throw new RuntimeException("队列已满!");
            loopQueue[tail] = element;
            tail = (tail + 1) % loopQueue.length;
            size++;
        }

        public E poll() {
            if (isEmpty()) throw new RuntimeException("队列空了!");
            E value = loopQueue[front];
            loopQueue[front] = null;
            front = (front + 1) % loopQueue.length;
            size--;
            return value;
        }

        public E getTailElement() {
            return loopQueue[tail - 1 >= 0 ? tail - 1 : tail + loopQueue.length - 1];
        }

        public E getFrontElement() {
            return loopQueue[front];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int temp = front;
            while (temp != tail) {
                sb.append(loopQueue[temp]);
                sb.append(" ");
                temp = (temp + 1) % loopQueue.length;
            }
            return sb.toString();
        }
    }
}
