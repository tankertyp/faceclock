package com.pingfly.faceclock.bluetooth.bean;

public class DataItem<T> {

    private int  type;
    private T data;

    public DataItem(){}

    public DataItem(int type, T data){
        this.data = data;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setType(int type) {
        this.type = type;
    }

}
