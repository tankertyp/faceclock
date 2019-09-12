package com.pingfly.faceclock.bluetooth.bean;

import java.util.ArrayList;

public class MessageModel {

    private ArrayList<DataItem> dataItems;

    public  enum TYPE{
        RECEIVER_TXT,
        SEND_TXT
    }

    public MessageModel(){}

    public MessageModel(ArrayList<DataItem> dataItems){
        this.dataItems = dataItems;
    }

    public void setDataItems(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    public ArrayList<DataItem> getDataItems() {
        return dataItems;
    }
}
