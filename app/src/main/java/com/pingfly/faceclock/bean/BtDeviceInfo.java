package com.pingfly.faceclock.bean;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BtDeviceInfo implements Parcelable {

    //public String name; // 蓝牙设备的名称
    //public String address; // 蓝牙设备的MAC地址
    //public int state; // 蓝牙设备的绑定状态

    private int deviceId;//ID
    private String deviceAddress;//设备地址
    private String identificationName;//标识名称，即设备名称
    //private String joinTime;//加入时间
    private boolean isOnline;//是否在线
    private BluetoothDevice bluetoothDevice;

    public BtDeviceInfo() {

    }

    protected BtDeviceInfo(Parcel in) {
        deviceId = in.readInt();
        deviceAddress = in.readString();
        identificationName = in.readString();
        //joinTime = in.readString();
        isOnline = in.readByte() != 0;
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<BtDeviceInfo> CREATOR = new Creator<BtDeviceInfo>() {
        @Override
        public BtDeviceInfo createFromParcel(Parcel in) {
            return new BtDeviceInfo(in);
        }

        @Override
        public BtDeviceInfo[] newArray(int size) {
            return new BtDeviceInfo[size];
        }
    };

    public int getDeviceId() {
        return deviceId;
    }

    public BtDeviceInfo setBtDeviceId(int btDeviceId) {
        this.deviceId = btDeviceId;
        return this;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public BtDeviceInfo setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        return this;
    }


    public String getIdentificationName() {
        return identificationName;
    }

    public BtDeviceInfo setIdentificationName(String identificationName) {
        this.identificationName = identificationName;
        return this;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public BtDeviceInfo setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }



    @Override
    public String toString() {
        return "BtDeviceInfo{" +
                "deviceId=" + deviceId +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", identificationName='" + identificationName + '\'' +
                ", isOnline=" + isOnline +
                ", bluetoothDevice=" + bluetoothDevice +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(deviceId);
        //parcel.writeString(friendname);
        //parcel.writeString(friendIconUrl);
        parcel.writeString(deviceAddress);
        parcel.writeString(identificationName);
        //parcel.writeString(joinTime);
        parcel.writeByte((byte) (isOnline ? 1 : 0));
        parcel.writeParcelable(bluetoothDevice, i);
    }


}
