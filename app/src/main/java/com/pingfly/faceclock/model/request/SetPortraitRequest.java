package com.pingfly.faceclock.model.request;

public class SetPortraitRequest {

    private String portraitUri;


    public SetPortraitRequest(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }
}
