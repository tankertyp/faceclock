package com.pingfly.faceclock.manager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

import retrofit2.adapter.rxjava.HttpException;


import java.util.HashMap;
import java.util.Map;

// 在 Android 系统中，广播（Broadcast）是在组件之间传播数据的一种机制。
// 这些组件可以位于不同的进程中，起到进程间通信的作用

/**
 * @描述 广播管理
 */
public class BroadcastManager {
    private Context mContext;
    private static BroadcastManager mInstance;
    private Map<String, BroadcastReceiver> mReceiverMap;

    private BroadcastManager(Context context) {
        mContext = context.getApplicationContext();
        mReceiverMap = new HashMap<>();
    }

    public static BroadcastManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BroadcastManager.class) {
                if (mInstance == null)
                    mInstance = new BroadcastManager(context);
            }
        }
        return mInstance;
    }

    /**
     * 注册
     */
    // BroadcastReceiver 是对发送出来的 Broadcast 进行过滤、接受和响应的组件。
    public void register(String action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            mContext.registerReceiver(receiver, filter);
            // action为要发送的消息，
            mReceiverMap.put(action, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     */
    public void sendBroadcast(String action) {
        // 使用sendBroadcast发送一个Intent对象，其中的Intent对象的action名
        sendBroadcast(action, "");
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param obj    参数
     */
    public void sendBroadcastWithObject(String action, Object obj) { // Object接口用于普通对象的传递
        try {
            Intent intent = new Intent();
            intent.setAction(action);   // action为将要发送的消息
            intent.putExtra("result", JsonManager.beanToJson(obj));
            mContext.sendBroadcast(intent);
        } catch (HttpException e) {

            e.printStackTrace();
        }
    }

    /**
     *
     * @param action
     * @param obj
     */
    public void sendBroadcast(String action, Parcelable obj) {// Parcel用来完成数据的序列化传递。
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("result", obj);
        mContext.sendBroadcast(intent);
    }

    /**
     * 发送参数为 String 的数据广播
     *
     * @param action
     * @param s
     */
    public void sendBroadcast(String action, String s) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("String", s);
        mContext.sendBroadcast(intent);
    }


    /**
     * 注销广播
     *
     * @param action
     */
    public void unregister(String action) {
        if (mReceiverMap != null) {
            BroadcastReceiver receiver = mReceiverMap.remove(action);
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
        }
    }
}