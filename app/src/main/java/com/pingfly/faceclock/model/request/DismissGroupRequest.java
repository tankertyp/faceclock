package com.pingfly.faceclock.model.request;

public class DismissGroupRequest {

    private String groupId;

    public DismissGroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
