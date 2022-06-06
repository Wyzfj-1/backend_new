package com.wsn.powerstrip.common.pattern.observerPattern;

/**
 * 观察者
 * 由于观察者可能观察多个对象，因此这里并不让观察者储存对象，当被观察对象动作时，直接将自己传入到update方法中即可
 * @Author: 夏星毅
 * @Modified wangzilinn@gmail.com
 */
public interface Observer<S extends Observable<S, O>, O extends Observer<S, O>> {
    /**
     * 根据不同的观察对象执行观察者动作
     * @param subject 观察者对象
     */
    void update(S subject);
}
