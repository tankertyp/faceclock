package com.pingfly.faceclock.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pingfly.faceclock.alarmclock.domain.ActivityCollection;

public class AlarmBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollection.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollection.remove(this);
    }

    protected void finishAll(){
        ActivityCollection.finishAll();
    }

}
