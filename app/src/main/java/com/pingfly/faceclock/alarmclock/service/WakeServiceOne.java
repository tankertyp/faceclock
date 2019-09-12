package com.pingfly.faceclock.alarmclock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.pingfly.faceclock.util.LogUtils;

public class WakeServiceOne extends Service {

    private static final String TAG = "WakeServiceOne";

    public WakeServiceOne() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("bad","服务一被启动");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        LogUtils.d("bad", "onTaskRemoved:进程被杀死了");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtils.d("bad", "服务一ontrim");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
