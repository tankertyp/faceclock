package com.pingfly.faceclock.model.data;

import com.pingfly.faceclock.model.response.GetGroupMemberResponse;

public class GetGroupMemberModel {
    GetGroupMemberResponse mGetGroupMemberResponse;
    String mGroupId;

    public GetGroupMemberModel(GetGroupMemberResponse getGroupMemberResponse, String groupId) {
        mGetGroupMemberResponse = getGroupMemberResponse;
        mGroupId = groupId;
    }

    public GetGroupMemberResponse getGetGroupMemberResponse() {
        return mGetGroupMemberResponse;
    }

    public void setGetGroupMemberResponse(GetGroupMemberResponse getGroupMemberResponse) {
        mGetGroupMemberResponse = getGroupMemberResponse;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }
}
