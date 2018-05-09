package com.rzm.myeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

    }

    public void set(View view) {
        EventTransition.getInstance().post("哈哈");
    }
}
