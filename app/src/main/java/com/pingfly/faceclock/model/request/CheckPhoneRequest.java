package com.pingfly.faceclock.model.request;

public class CheckPhoneRequest {
    private String phone;

    private String region;

    public CheckPhoneRequest(String phone, String region) {
        this.phone = phone;
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
