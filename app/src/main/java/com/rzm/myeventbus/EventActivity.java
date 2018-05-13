package com.rzm.myeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        EventBus.getDefault().register(this);
    }

    public void set(View view) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                EventTransition.getInstance().post("哈哈");
                EventBus.getDefault().post("EventBus");
            }
        }.start();

    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,priority = 50,sticky = true)
    public void testEventActivity(String msg){
    }
}
