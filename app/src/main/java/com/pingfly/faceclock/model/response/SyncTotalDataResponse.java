package com.pingfly.faceclock.model.response;

public class SyncTotalDataResponse {

    private int code;

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

    public static class ResultEntity {
        private long version;
        /**
         * id : 4725
         * name : 李峰
         * portraitUri : http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP
         * timestamp : 1471234299530
         */

        private UserEntity user;

        public void setVersion(long version) {
            this.version = version;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public long getVersion() {
            return version;
        }

        public UserEntity getUser() {
            return user;
        }


        public static class UserEntity {
            private int id;
            private String name;
            private String portraitUri;
            private long timestamp;

            public void setId(int id) {
                this.id = id;
            }

            public void setname(String name) {
                this.name = name;
            }

            public void setPortraitUri(String portraitUri) {
                this.portraitUri = portraitUri;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public int getId() {
                return id;
            }

            public String getname() {
                return name;
            }

            public String getPortraitUri() {
                return portraitUri;
            }

            public long getTimestamp() {
                return timestamp;
            }
        }
    }
}
