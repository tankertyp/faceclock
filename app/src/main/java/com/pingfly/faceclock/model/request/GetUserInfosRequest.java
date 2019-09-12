package com.pingfly.faceclock.model.request;

import java.util.List;

public class GetUserInfosRequest {
    private List<String> querystring;

    public List<String> getQuerystring() {
        return querystring;
    }

    public void setQuerystring(List<String> querystring) {
        this.querystring = querystring;
    }

    public GetUserInfosRequest(List<String> querystring) {
        this.querystring = querystring;
    }
}
