package com.pingfly.faceclock.model.request;

public class SendCodeByEmailRequest {
    private String email;

    public SendCodeByEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
