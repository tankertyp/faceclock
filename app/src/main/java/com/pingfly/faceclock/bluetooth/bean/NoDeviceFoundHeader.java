package com.pingfly.faceclock.bluetooth.bean;

public class NoDeviceFoundHeader {

    private String headerName;

    public NoDeviceFoundHeader(){}

    public NoDeviceFoundHeader(String headerName){
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
