package com.rzm.myeventbus;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class AsyncTransfer implements Runnable {

    private final Object event;
    private final Subscription subscription;
    private final static ExecutorService executorService = Executors.newCachedThreadPool();

    AsyncTransfer(Subscription subscription, Object event) {
        this.subscription = subscription;
        this.event = event;
    }

    public static void enqueue(Subscription subscription, Object event) {
        AsyncTransfer transfer = new AsyncTransfer(subscription,event);
        executorService.execute(transfer);
    }

    @Override
    public void run() {
        try {
            subscription.subscriberMethod.method.invoke(subscription,event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}