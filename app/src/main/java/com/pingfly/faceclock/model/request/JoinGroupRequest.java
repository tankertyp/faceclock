package com.pingfly.faceclock.model.request;

public class JoinGroupRequest {
    private String groupId;

    public JoinGroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
