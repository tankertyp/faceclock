package com.pingfly.faceclock.bean;

import android.bluetooth.BluetoothDevice;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class AlarmClock extends DataSupport implements Serializable {

    private int id;         // 闹钟id
    private String userId;    // 对应于User表的id
    private int hour;       // 小时
    private int minute;     // 分钟
    private String weeks;   // 周期
    private String tag;     // 标签
    private String repeat;  // 重复
    private String ringName;   // 铃声名
    private String ringUrl;    // 铃声地址
    private int ringPager;  // 铃声选择标记界面
    private int volume;     // 音量
    private boolean onOff;  // 闹钟开关
    private String deviceAddress;//设备地址
    private String identificationName;//标识名称，即设备名称
    private BluetoothDevice bluetoothDevice;
    private boolean isSend;
    private String sendTime;


    public String getDeviceAddress() {
        return deviceAddress;
    }
    public AlarmClock setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        return this;
    }

    public String getIdentificationName() {
        return identificationName;
    }
    public AlarmClock setIdentificationName(String identificationName) {
        this.identificationName = identificationName;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId() {
        this.userId = userId;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRingName() {
        return ringName;
    }

    public void setRingName(String ringName) {
        this.ringName = ringName;
    }

    public String getRingUrl() {
        return ringUrl;
    }

    public int getRingPager() {
        return ringPager;
    }

    public void setRingPager(int ringPager) {
        this.ringPager = ringPager;
    }

    public void setRingUrl(String ringUrl) {
        this.ringUrl = ringUrl;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isOnOff() {
        return onOff;
    }

    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
    }


    // 由于闹钟信息要发给nanopi,所以在闹钟实例中进行蓝牙配置
    public BluetoothDevice getBtDevice() {
        return bluetoothDevice;
    }

    public AlarmClock setBtDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        return this;
    }



    public boolean isSend() {
        return isSend;
    }

    public AlarmClock setSend(boolean send) {
        isSend = send;
        return this;
    }

    public String getSendTime() {
        return sendTime;
    }

    public AlarmClock setSendTime(String sendTime) {
        this.sendTime = sendTime;
        return this;
    }

}
