package com.pingfly.faceclock.model.request;

public class CheckEmailRequest {

    private String mail;

    public CheckEmailRequest(String mail) {
      this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


}
