package com.pingfly.faceclock.model.request;

public class RegisterRequest {

    private String name;

    private String password;

    private String verification_token;

    public RegisterRequest(String name, String password, String verification_token) {
        this.name = name;
        this.password = password;
        this.verification_token = verification_token;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerification_token() {
        return verification_token;
    }

    public void setVerification_token(String verification_token) {
        this.verification_token = verification_token;
    }

}
