package com.pingfly.faceclock.alarmclock.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.alarmclock.domain.AlarmClock;
import com.pingfly.faceclock.alarmclock.domain.AlarmInfo;
import com.pingfly.faceclock.alarmclock.service.AlarmRingService;
import com.pingfly.faceclock.alarmclock.util.PrefUtils;
import com.pingfly.faceclock.ui.activity.WakeUpActivity;
import com.pingfly.faceclock.util.LogUtils;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {


    private int lazylevel;
    private String tag;
    private Context context;
    private int A;
    private int B;
    private int id;
    private AlarmManager alarmManager;
    private String getid;
    private String resid;
    private int[] dayOfWeek;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        LogUtils.d("alarm", "收到广播");
        getid = intent.getStringExtra("getid");
        // 在两个Activity之间传递数据，最终都是通过Intent传递，但设置数据和保存数据方式有两种(使用Intent 和 Bundle)
        //Bundle bundle = intent.getExtras();
        Bundle bundle = intent.getBundleExtra("bundle");

        // key-value形式
        if (bundle != null) {
            AlarmInfo currentAlarm = (AlarmInfo) Objects.requireNonNull(bundle).getSerializable("alarminfo");

            lazylevel = Objects.requireNonNull(currentAlarm).getLazyLevel();

            tag = currentAlarm.getTag();
            dayOfWeek = currentAlarm.getDayOfWeek();
            resid = currentAlarm.getRingResId();
        }
        id = intent.getIntExtra("alarmid", 0);
        //先进行判断今天该闹钟是否该响
        //需要的数据是 赖床级数 标签 铃声
        LogUtils.d("alarm", dayOfWeek[0] + "dayofweek0");
        LogUtils.d("alarm", "cancel" + intent.getBooleanExtra("cancel", false));
        if (intent.getBooleanExtra("cancel", false)) {
            cancelAlarm(intent);
            return;
        }
        if (dayOfWeek[0] == 0) {
            wakePhoneAndUnlock();
            ringAlarm();
            PrefUtils.putBoolean(context, getid, false);
        } else {
            LogUtils.d("alarm", "执行else" + dayOfWeek.length);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            for (int i = 0; i < dayOfWeek.length; i++) {
                LogUtils.d("alarm", dayOfWeek[i] + ";" + currentDay);
                if (dayOfWeek[i] == 7) {
                    dayOfWeek[i] = 0;
                }
                if (currentDay == dayOfWeek[i]) {
                    LogUtils.d("alarm", dayOfWeek[i] + ";" + currentDay);
                    wakePhoneAndUnlock();
                    ringAlarm();
                }
            }
            runAlarmAgain(intent, getid);
        }
    }

    private void cancelAlarm(Intent intent) {
        LogUtils.d("alarm", "取消闹钟");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }

    private void runAlarmAgain(Intent intent, String id) {

        //未完成
        LogUtils.d("alarm", "再次启动闹钟");
        AlarmClock alarmClock = new AlarmClock(context);
        alarmClock.turnAlarm(null, getid, true);


        /*alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=PendingIntent.getBroadcast(context,id,intent,0);
        Calendar c=Calendar.getInstance();
       if(hour!=-1&&minute!=-1){
           c.set(Calendar.HOUR_OF_DAY,hour);
           c.set(Calendar.MINUTE,minute);
           c.set(Calendar.SECOND,0);
           c.set(Calendar.MILLISECOND, 0);
       }else{
           c.setTimeInMillis(System.currentTimeMillis());
       }
        if(c.getTimeInMillis()<System.currentTimeMillis()){
            if(Build.VERSION.SDK_INT>=19)
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }else{
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }
        }else {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }
        }*/
    }

    //点亮屏幕并解锁
    private void wakePhoneAndUnlock() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock mWakelock = Objects.requireNonNull(pm).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "WakeLock");
        mWakelock.acquire(10 * 60 * 1000L /*10 minutes*/);//唤醒屏幕
//......
        KeyguardManager mManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKeyguardLock = Objects.requireNonNull(mManager).newKeyguardLock("Lock");
        //让键盘锁失效
        mKeyguardLock.disableKeyguard();
        mWakelock.release();//释放
    }

    private void ringAlarm() {
        //if(PrefUtils.getBoolean(context, ConsUtils.SHOULD_WETHER_CLOSE,false)){
        //如果用户关闭了天气 不再弹出Activity;
        //}else{
        //打开天气提示
        Intent intent = new Intent(context, WakeUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        //}

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Date date = calendar.getTime();
        LogUtils.d("alarm", "收到广播的时间" + date.toString());
        showAlarmDialog();
        LogUtils.d("alarm", "赖床指数" + lazylevel + "," + tag + ",");
    }


    /**
     * 展示闹钟对话框
     */
    private void showAlarmDialog() {

        //启动并绑定AlarmRingService
        final Intent service = new Intent(context, AlarmRingService.class);

        service.putExtra("resid", resid);
        context.startService(service);

        LogUtils.d("alarm", "初始化dialog");
        View edit = View.inflate(context, R.layout.dialog_tag, null);
        final EditText Input = (EditText) edit.findViewById(R.id.et_tag);
        Input.setInputType(InputType.TYPE_CLASS_NUMBER);

        // 对话框构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(tag);
        String message = "美好的一天开始啦";
        //当赖床指数高于0时
        int rightAnswer = 0;
        if (lazylevel > 0) {
            rightAnswer = youCantSleep();
            builder.setView(edit);
            message = "还睡？！来做题：" + A + "×" + B + "=?";
        }
        builder.setMessage(message);
        builder.setCancelable(false);
        final int finalRightAnswer = rightAnswer;
        builder.setPositiveButton("确定", (dialog, which) -> {
            Field field = null;
            try {
                field = dialog.getClass()
                        .getSuperclass().getDeclaredField(
                                "mShowing");
                field.setAccessible(true);
                //   将mShowing变量设为false，表示对话框已关闭
                field.set(dialog, false);
                dialog.dismiss();

            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            if (lazylevel == 0) {
                try {
                    Objects.requireNonNull(field).set(dialog, true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();


                // context.unbindService(conn);
                context.stopService(service);


            } else {
                if (Input.getText().toString().equals("") || Input.getText().toString() == null) {
                    Toast.makeText(context, "想交白卷？~", Toast.LENGTH_SHORT).show();
                } else {
                    int result = Integer.parseInt(Input.getText().toString());
                    if (result == finalRightAnswer) {
                        try {
                            Objects.requireNonNull(field).set(dialog, true);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        //正确
                        dialog.dismiss();
                        // context.unbindService(conn);
                        context.stopService(service);
                        Toast.makeText(context, "Morning~", Toast.LENGTH_SHORT).show();
                    } else {
                        Input.setText("");
                        Toast.makeText(context, "再清醒一点要死吗？", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams params = Objects.requireNonNull(dialog.getWindow())
                .getAttributes();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        LogUtils.d("alarm", "dialogshow");
        dialog.show();
    }

    private int youCantSleep() {
        Random a = new Random();
        if (lazylevel == 1) {
            A = a.nextInt(20) + 5;
            B = a.nextInt(20) + 5;
        } else if (lazylevel == 2) {
            A = a.nextInt(99) + 1;
            B = a.nextInt(99) + 1;
            while (B < 50) B = B + 10;
        } else if (lazylevel == 3) {
            A = a.nextInt(200) + 1;
            B = a.nextInt(200) + 1;
            while (B < 80) {
                B = B + 10;
            }
            while (A < 80) {
                A = A + 10;
            }
        } else if (lazylevel == 4) {
            A = a.nextInt(500) + 1;
            B = a.nextInt(500) + 1;
            while (B < 80) {
                B = B + 10;
            }
            while (A < 200) {
                A = A + 30;
            }
        }

        return A * B;
    }

}
