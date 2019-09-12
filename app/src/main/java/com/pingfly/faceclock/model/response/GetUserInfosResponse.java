package com.pingfly.faceclock.model.response;

import java.util.List;

public class GetUserInfosResponse {

    /**
     * code : 200
     * result : [{"id":"t1hWCOGvX","name":"阿明","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Fq_AkfurWIXCDnclIEZBwEoc988R"},{"id":"LEU82p5Zk","name":"李涛","portraitUri":""},{"id":"jkirN8Yfq","name":"李小黎","portraitUri":""}]
     */

    private int code;
    /**
     * id : t1hWCOGvX
     * name : 阿明
     * portraitUri : http://7xogjk.com1.z0.glb.clouddn.com/Fq_AkfurWIXCDnclIEZBwEoc988R
     */

    private List<ResultEntity> result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity {
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
