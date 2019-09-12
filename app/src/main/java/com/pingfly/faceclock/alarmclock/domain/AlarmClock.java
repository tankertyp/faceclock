package com.pingfly.faceclock.alarmclock.domain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.pingfly.faceclock.alarmclock.dao.AlarmInfoDao;
import com.pingfly.faceclock.alarmclock.receiver.AlarmReceiver;
import com.pingfly.faceclock.util.LogUtils;

import java.util.Calendar;
import java.util.Date;


public class AlarmClock {

    private AlarmInfo alarmInfo;
    private Context context;
    private final AlarmInfoDao dao;

    public AlarmClock(Context context) {
        this.context = context;
        dao = new AlarmInfoDao(context);
    }

    public void turnAlarm(AlarmInfo alarmInfo,String AlarmID,Boolean isOn){
        if(alarmInfo==null){
            LogUtils.d("alarm","传入AlarmInfo不为空");
            alarmInfo=dao.findById(AlarmID);
        }
        this.alarmInfo=alarmInfo;
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int id=dao.getOnlyId(alarmInfo);


        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle=new Bundle();
        // 传入AlarmInfo对象
        bundle.putSerializable("alarminfo", alarmInfo);
        //intent.putExtras(bundle);    //将bundle传入intent中
        intent.putExtra("bundle",bundle);

        /* ********************* 关键部位 **********************************/
        intent.setAction("com.pingfly.RING_ALARM");


        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("alarmid", id);
        intent.putExtra("cancel",false);
        intent.putExtra("getid",alarmInfo.getId());
        LogUtils.d("alarm", "id" + id);
        //每个闹钟不同的pi
        PendingIntent pi= PendingIntent.getBroadcast(context,id, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if(isOn){
            startAlarm(mAlarmManager,pi);
        }else{
            cancelAlarm(intent);
        }
    }

    private void cancelAlarm(Intent intent) {
        LogUtils.d("alarm","取消闹钟");
        intent.putExtra("cancel",true);
        context.sendBroadcast(intent);
    }

    private void startAlarm(AlarmManager mAlarmManager, PendingIntent pi){
        LogUtils.d("alarm","启动闹钟");
        Calendar c=Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,alarmInfo.getHour());
        c.set(Calendar.MINUTE,alarmInfo.getMinute());
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
        //  Log.d("alarm", "当前系统版本" + Build.VERSION.SDK_INT);
        if(c.getTimeInMillis()<System.currentTimeMillis()){
            if(Build.VERSION.SDK_INT>=19)
            {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }else{
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }
        }else{
            if(Build.VERSION.SDK_INT>=19)
            {
                LogUtils.d("alarm","执行定时任务");
                Date date=c.getTime();
                LogUtils.d("alarm","定时的时间是"+date.toString());
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }else{
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }
        }

    }

}
