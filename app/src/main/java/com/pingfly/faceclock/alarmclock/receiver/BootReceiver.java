package com.pingfly.faceclock.alarmclock.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pingfly.faceclock.alarmclock.dao.AlarmInfoDao;
import com.pingfly.faceclock.alarmclock.domain.AlarmClock;
import com.pingfly.faceclock.alarmclock.domain.AlarmInfo;
import com.pingfly.faceclock.alarmclock.util.PrefUtils;
import com.pingfly.faceclock.util.LogUtils;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("alarm","Boot收到广播");
        checkAndStartAlarm(context);
    }

    //开机时检查是否有闹钟需要开启
    private void checkAndStartAlarm(Context context) {
        LogUtils.d("alarm","开始检查是否有闹钟");
        AlarmInfoDao dao=new AlarmInfoDao(context);
        ArrayList<AlarmInfo> list= (ArrayList<AlarmInfo>) dao.getAllInfo();
        AlarmClock clock=new AlarmClock(context);
        for (AlarmInfo alarmInfo:list) {
            if(PrefUtils.getBoolean(context, alarmInfo.getId(), true)){
                LogUtils.d("alarm","有闹钟，开启");
                clock.turnAlarm(alarmInfo,null,true);
            }

        }
    }

}
