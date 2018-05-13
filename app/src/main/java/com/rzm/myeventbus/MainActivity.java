package com.rzm.myeventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private TextView mTextView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        mTextView1 = findViewById(R.id.text1);
        EventTransition.getInstance().register(this);
        EventBus.getDefault().register(this);
    }

    public void jump(View view) {
        startActivity(new Intent(getApplicationContext(),EventActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 50,sticky = true)
    public void test1(String msg){
        mTextView.setText(msg);
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,priority = 50,sticky = true)
    public void test2(String msg){
        mTextView1.setText(msg);
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,priority = 50,sticky = true)
    public void test3(String msg){
        mTextView1.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventTransition.getInstance().unRegister(this);
        EventBus.getDefault().unregister(this);
    }
}
