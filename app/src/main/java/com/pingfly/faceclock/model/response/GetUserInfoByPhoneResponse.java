package com.pingfly.faceclock.model.response;

import java.io.Serializable;

public class GetUserInfoByPhoneResponse {


    /**
     * code : 200
     * result : {"id":"10YVscJI3","name":"阿明","portraitUri":""}
     */

    private int code;
    /**
     * id : 10YVscJI3
     * name : 阿明
     * portraitUri :
     */

    private ResultEntity result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity implements Serializable {
        private String id;
        private String name;
        private String portraitUri;

        public void setId(String id) {
            this.id = id;
        }

        public void setname(String name) {
            this.name = name;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public String getId() {
            return id;
        }

        public String getname() {
            return name;
        }

        public String getPortraitUri() {
            return portraitUri;
        }
    }
}
