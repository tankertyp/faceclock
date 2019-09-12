package com.pingfly.faceclock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.AlarmClock;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.util.MyUtils;

public class AlarmClockBroadcast extends BroadcastReceiver {

    /**
     * Log tag ：AlarmClockBroadcast
     */
    private static final String LOG_TAG = "AlarmClockBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmClock alarmClock = intent
                .getParcelableExtra(AppConst.ALARM_CLOCK);
        if (alarmClock != null) {
            // 单次响铃
            if (alarmClock.getWeeks() == null) {
                DBManager.getInstance().updateAlarmClock(alarmClock);

                Intent i = new Intent("com.kaku.weac.AlarmClockOff");
                context.sendBroadcast(i);
            } else {
                // 重复周期闹钟
                MyUtils.startAlarmClock(context, alarmClock);
            }
        }
    }
}
