package com.pingfly.faceclock.model.request;

public class QuitGroupRequest {

    private String groupId;

    public QuitGroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
