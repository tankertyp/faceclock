package com.pingfly.faceclock.ui.presenter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.AlarmClock;
import com.pingfly.faceclock.bluetooth.BluetoothService;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IAlarmClockNewAtView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.MyUtils;
import com.pingfly.faceclock.util.ToastUtils;
import com.pingfly.faceclock.util.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pingfly.faceclock.app.AppConst.TAG;

public class AlarmClockNewAtPresenter extends BasePresenter<IAlarmClockNewAtView> {


    private AlarmClock mAlarmClock;
    private List<AlarmClock> mAlarmClockList = new ArrayList<>();

    public AlarmClockNewAtPresenter(BaseActivity context) {
        super(context);
        //int mAlarmClockId = alarmClockId;
    }

    private String to3Digits(String str) {
        switch (str.length()){
            case 1:
                str = "00"+str;
                break;
            case 2:
                str = "0"+str;
                break;
            case 3:
                break;
        }
        return str;
    }

    private String to7Digits (String str) {

        String str1 = "";
        boolean isSunday = str.contains("7");
        boolean isMonday = str.contains("1");
        boolean isTuesday = str.contains("2");
        boolean isWednesday = str.contains("3");
        boolean isThursday = str.contains("4");
        boolean isFriday = str.contains("5");
        boolean isSaturday = str.contains("6");

        if(isSunday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        if(isMonday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        if(isTuesday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        if(isWednesday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        if(isThursday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        if(isFriday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        if(isSaturday) {
            str1 += str1 + '1';
        } else {
            str1 += str1 + '0';
        }
        return str1;
    }

    /**
     * 点击保存按钮即新建闹钟成功，默认开启，同时通过蓝牙发送新建成功的闹钟信息给nanopi
     */
    public void sendAlarmClockInfo() {

        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));

        int id = mAlarmClock.getId();
        String id_str = Integer.toString(id);
        id_str = to3Digits(id_str);

        String weeks = mAlarmClock.getWeeks();
        String weeks_str = to7Digits(weeks);

        int volume = mAlarmClock.getVolume();
        String volume_str = Integer.toString(volume);
        volume_str = to3Digits(volume_str);
        String ringName = mAlarmClock.getRingName();

        String AlarmClockNewCommand = "200" + id_str + weeks_str + ringName;
        // 将新建闹钟的string通过bluetooth socket发送给nanopi
        //sendMessage(AlarmClockNewCommand);
    }


    /**
     * 计算显示倒计时信息
     */
    public void displayCountDown() {
        // 取得下次响铃时间
        long nextTime = MyUtils.calculateNextTime(mAlarmClock.getHour(),
                mAlarmClock.getMinute(), mAlarmClock.getWeeks());
        // 系统时间
        long now = System.currentTimeMillis();
        // 距离下次响铃间隔毫秒数
        long ms = nextTime - now;

        // 单位秒
        int ss = 1000;
        // 单位分
        int mm = ss * 60;
        // 单位小时
        int hh = mm * 60;
        // 单位天
        int dd = hh * 24;

        // 不计算秒，故响铃间隔加一分钟
        ms += mm;
        // 剩余天数
        long remainDay = ms / dd;
        // 剩余小时
        long remainHour = (ms - remainDay * dd) / hh;
        // 剩余分钟
        long remainMinute = (ms - remainDay * dd - remainHour * hh) / mm;

        // 当剩余天数大于0时显示【X天X小时X分】格式
        // 响铃倒计时
        String countDown;
        if (remainDay > 0) {
            countDown = mContext.getString(R.string.countdown_day_hour_minute);
            getView().getTvTimePicker().setText(String.format(countDown, remainDay,
                    remainHour, remainMinute));
            // 当剩余小时大于0时显示【X小时X分】格式
        } else if (remainHour > 0) {
            countDown = mContext.getResources()
                    .getString(R.string.countdown_hour_minute);
            getView().getTvTimePicker().setText(String.format(countDown, remainHour,
                    remainMinute));
        } else {
            // 当剩余分钟不等于0时显示【X分钟】格式
            if (remainMinute != 0) {
                countDown = mContext.getString(R.string.countdown_minute);
                getView().getTvTimePicker().setText(String.format(countDown, remainMinute));
                // 当剩余分钟等于0时，显示【1天0小时0分】
            } else {
                countDown = mContext.getString(R.string.countdown_day_hour_minute);
                getView().getTvTimePicker().setText(String.format(countDown, 1, 0, 0));
            }

        }
    }

}

