package com.rzm.myeventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventTransition {

    /**
     * key为Event参数的类（注解方法中传入的参数class类型），value存放的是Subscription的集合，Subscription包含两个属性
     * 一个是subscriber订阅者(在哪个activity注册的)，一个是Subscriber注解方法（所有被注解的方法），也就是说，这个集合是
     * 根据参数类型来存储当前类中所有的方法对象
     */
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> mSubscriptionsByEventType;

    /**
     * key是所有的订阅者，value是所有订阅者里面方法的参数的class文件，这个集合根据当前类class来存储当前页面所有的参数类型
     */
    private final Map<Object, List<Class<?>>> mTypesBySubscriber;

    private static EventTransition mInstance;

    private EventTransition(){
        mSubscriptionsByEventType =new HashMap<>();
        mTypesBySubscriber = new HashMap<>();
    }

    public static EventTransition getInstance() {
        if (mInstance == null) {
            synchronized (EventTransition.class) {
                if (mInstance == null) {
                    mInstance = new EventTransition();
                }
            }
        }
        return mInstance;
    }

    public void register(Object subscriber) {
        if (subscriber == null){
            throw new NullPointerException("register object cannot be null");
        }

        //这个集合中将存储当前页面的所有被标记的方法封装成的对象，这个对象包括方法名方法参数以及一些注解的参数
        List<SubscriberMethod> subscriberMethodList = new ArrayList<>();
        //解析所有方法封装成SubscriberMethod集合
        Class<?> clazz = subscriber.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe != null){
                //获取到方法的所有参数的class
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 1)
                    throw new IllegalArgumentException("parameter can only have one");
                SubscriberMethod subscriberMethod = new SubscriberMethod
                        (method,parameterTypes[0],subscribe.threadMode(),subscribe.priority(),subscribe.sticky());
                subscriberMethodList.add(subscriberMethod);
            }
        }
        //按照规则存放到subscriptionByEventType中
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethodList) {
                subscribe(subscriber, subscriberMethod);
            }
        }
    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        //方法参数的Class
        Class<?> eventType = subscriberMethod.eventType;
        CopyOnWriteArrayList<Subscription> subscriptions = mSubscriptionsByEventType.get(eventType);
        if (subscriptions == null){
            subscriptions = new CopyOnWriteArrayList<>();
            mSubscriptionsByEventType.put(eventType,subscriptions);
        }

        //判断优先级

        //
        Subscription subscription = new Subscription(subscriber,subscriberMethod);
        subscriptions.add(subscription);

        //为unRegister做准备,存放每一个被注册页面的所有方法的参数类型
        List<Class<?>> eventTypes = mTypesBySubscriber.get(subscriber);
        if (eventTypes == null){
            eventTypes = new ArrayList<>();
            mTypesBySubscriber.put(subscriber,eventTypes);
        }
        if (!eventTypes.contains(eventType)){
            eventTypes.add(eventType);
        }
    }

    public void unRegister(Object subscribe) {
        //获取到每一个页面（如Activity）中所有被注解的方法的参数class
        List<Class<?>> eventTypes = mTypesBySubscriber.get(subscribe);
        if (eventTypes != null){
            for (Class<?> eventType : eventTypes) {
                removeObject(eventType,subscribe);
            }
        }
    }

    private void removeObject(Class<?> eventType, Object subscriber) {
        //根据当前页面所有的参数类型获取到当前页面所有的注解方法
        List<Subscription> subscriptions = mSubscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }
}















