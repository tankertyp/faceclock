package com.pingfly.faceclock.model.request;

public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest(String region, String phone, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setPhone(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
