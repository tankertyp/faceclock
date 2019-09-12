package com.pingfly.faceclock.model.request;

public class SetNameRequest {

    private String name;

    public SetNameRequest(String name) {
        this.name = name;
    }


    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }
}
