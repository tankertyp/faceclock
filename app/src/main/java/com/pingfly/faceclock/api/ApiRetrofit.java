package com.pingfly.faceclock.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pingfly.faceclock.api.base.BaseApiRetrofit;
import com.pingfly.faceclock.model.request.AddGroupMemberRequest;
import com.pingfly.faceclock.model.request.AddToBlackListRequest;
import com.pingfly.faceclock.model.request.AgreeFriendsRequest;
import com.pingfly.faceclock.model.request.CheckEmailRequest;
import com.pingfly.faceclock.model.request.CheckPhoneRequest;
import com.pingfly.faceclock.model.request.CreateGroupRequest;
import com.pingfly.faceclock.model.request.DeleteFriendRequest;
import com.pingfly.faceclock.model.request.DeleteGroupMemberRequest;
import com.pingfly.faceclock.model.request.DismissGroupRequest;
import com.pingfly.faceclock.model.request.FriendInvitationRequest;
import com.pingfly.faceclock.model.request.JoinGroupRequest;
import com.pingfly.faceclock.model.request.LoginRequest;
import com.pingfly.faceclock.model.request.QuitGroupRequest;
import com.pingfly.faceclock.model.request.RegisterRequest;
import com.pingfly.faceclock.model.request.RemoveFromBlacklistRequest;
import com.pingfly.faceclock.model.request.ResetPasswordRequest;
import com.pingfly.faceclock.model.request.SendCodeByEmailRequest;
import com.pingfly.faceclock.model.request.SetFriendDisplayNameRequest;
import com.pingfly.faceclock.model.request.SetGroupDisplayNameRequest;
import com.pingfly.faceclock.model.request.SetGroupNameRequest;
import com.pingfly.faceclock.model.request.SetGroupPortraitRequest;
import com.pingfly.faceclock.model.request.SetNameRequest;
import com.pingfly.faceclock.model.request.ChangePasswordRequest;
import com.pingfly.faceclock.model.request.SendCodeRequest;
import com.pingfly.faceclock.model.request.SetPortraitRequest;
import com.pingfly.faceclock.model.request.VerifyCodeRequest;
import com.pingfly.faceclock.model.response.AddGroupMemberResponse;
import com.pingfly.faceclock.model.response.AddToBlackListResponse;
import com.pingfly.faceclock.model.response.AgreeFriendsResponse;
import com.pingfly.faceclock.model.response.ChangePasswordResponse;
import com.pingfly.faceclock.model.response.CheckEmailResponse;
import com.pingfly.faceclock.model.response.CheckPhoneResponse;
import com.pingfly.faceclock.model.response.CreateGroupResponse;
import com.pingfly.faceclock.model.response.DefaultConversationResponse;
import com.pingfly.faceclock.model.response.DeleteFriendResponse;
import com.pingfly.faceclock.model.response.DeleteGroupMemberResponse;
import com.pingfly.faceclock.model.response.FriendInvitationResponse;
import com.pingfly.faceclock.model.response.GetBlackListResponse;
import com.pingfly.faceclock.model.response.GetFriendInfoByIDResponse;
import com.pingfly.faceclock.model.response.GetGroupInfoResponse;
import com.pingfly.faceclock.model.response.GetGroupMemberResponse;
import com.pingfly.faceclock.model.response.GetGroupResponse;
import com.pingfly.faceclock.model.response.GetTokenResponse;
import com.pingfly.faceclock.model.response.GetUserInfoByIdResponse;
import com.pingfly.faceclock.model.response.GetUserInfoByPhoneResponse;
import com.pingfly.faceclock.model.response.GetUserInfosResponse;
import com.pingfly.faceclock.model.response.JoinGroupResponse;
import com.pingfly.faceclock.model.response.LoginResponse;
import com.pingfly.faceclock.model.response.QiNiuTokenResponse;
import com.pingfly.faceclock.model.response.QuitGroupResponse;
import com.pingfly.faceclock.model.response.RegisterResponse;
import com.pingfly.faceclock.model.response.RemoveFromBlackListResponse;
import com.pingfly.faceclock.model.response.ResetPasswordResponse;

import com.pingfly.faceclock.model.response.SendCodeByEmailResponse;
import com.pingfly.faceclock.model.response.SendCodeResponse;
import com.pingfly.faceclock.model.response.SetFriendDisplayNameResponse;
import com.pingfly.faceclock.model.response.SetGroupDisplayNameResponse;
import com.pingfly.faceclock.model.response.SetGroupNameResponse;
import com.pingfly.faceclock.model.response.SetGroupPortraitResponse;
import com.pingfly.faceclock.model.response.SetNameResponse;
import com.pingfly.faceclock.model.response.SetPortraitResponse;
import com.pingfly.faceclock.model.response.SyncTotalDataResponse;
import com.pingfly.faceclock.model.response.UserRelationshipResponse;
import com.pingfly.faceclock.model.response.VerifyCodeResponse;
import com.pingfly.faceclock.model.response.VersionResponse;


import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * @描述 使用Retrofit对网络请求进行配置
 */
public class ApiRetrofit extends BaseApiRetrofit {

    public MyApi mApi;
    public static ApiRetrofit mInstance;

    private ApiRetrofit() {
        super();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //在构造方法中完成对Retrofit接口的初始化
        // 生成一个“自定义的Retrofit接口”的代理对象,继而调用接口里面的方法,返回一个Observable泛型是接收的对象
        mApi = new Retrofit.Builder()
                .baseUrl(MyApi.BASE_URL)    //设置远程地址
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(MyApi.class);   // 自定义的Retrofit接口
    }

    public static ApiRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    private RequestBody getRequestBody(Object obj) {
        String route = new Gson().toJson(obj);
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), route);
        return body;
    }

    //登录
    public Observable<LoginResponse> login(String region, String phone, String password) {
        return mApi.login(getRequestBody(new LoginRequest(region, phone, password)));
    }

    //注册
    public Observable<CheckPhoneResponse> checkPhoneAvailable(String region, String phone) {
        return mApi.checkPhoneAvailable(getRequestBody(new CheckPhoneRequest(phone, region)));
    }

    public Observable<CheckEmailResponse> checkEmailAvailable(String email) {
        return mApi.checkEmailAvailable(getRequestBody(new CheckEmailRequest(email)));
    }

    public Observable<SendCodeResponse> sendCode(String region, String phone) {
        return mApi.sendCode(getRequestBody(new SendCodeRequest(region, phone)));
    }

    public Observable<SendCodeByEmailResponse> sendCodeByEmail(String email) {
        return mApi.sendCodeByEmail(getRequestBody(new SendCodeByEmailRequest(email)));
    }

    public Observable<VerifyCodeResponse> verifyCode(String region, String phone, String code) {
        return mApi.verifyCode(getRequestBody(new VerifyCodeRequest(region, phone, code)));
    }

    public Observable<RegisterResponse> register(String name, String password, String verification_token) {
        return mApi.register(getRequestBody(new RegisterRequest(name, password, verification_token)));
    }

    public Observable<GetTokenResponse> getToken() {
        return mApi.getToken();
    }

    //个人信息
    public Observable<SetNameResponse> setName(String name) {
        return mApi.setName(getRequestBody(new SetNameRequest(name)));
    }

    public Observable<SetPortraitResponse> setPortrait(String portraitUri) {
        return mApi.setPortrait(getRequestBody(new SetPortraitRequest(portraitUri)));
    }

    public Observable<ChangePasswordResponse> changePassword(String oldPassword, String newPassword) {
        return mApi.changePassword(getRequestBody(new ChangePasswordRequest(oldPassword, newPassword)));
    }

    /**
     * @param password           密码，6 到 20 个字节，不能包含空格
     * @param verification_token 调用 /user/verify_code 成功后返回的 activation_token
     */
    public Observable<ResetPasswordResponse> resetPassword(String password, String verification_token) {
        return mApi.resetPassword(getRequestBody(new ResetPasswordRequest(password, verification_token)));
    }

    //查询
    public Observable<GetUserInfoByIdResponse> getUserInfoById(String userid) {
        return mApi.getUserInfoById(userid);
    }

    public Observable<GetUserInfoByPhoneResponse> getUserInfoFromPhone(String region, String phone) {
        return mApi.getUserInfoFromPhone(region, phone);
    }

    //好友
    public Observable<FriendInvitationResponse> sendFriendInvitation(String userid, String addFriendMessage) {
        return mApi.sendFriendInvitation(getRequestBody(new FriendInvitationRequest(userid, addFriendMessage)));
    }

    public Observable<UserRelationshipResponse> getAllUserRelationship() {
        return mApi.getAllUserRelationship();
    }

    public Observable<GetFriendInfoByIDResponse> getFriendInfoByID(String userid) {
        return mApi.getFriendInfoByID(userid);
    }

    public Observable<AgreeFriendsResponse> agreeFriends(String friendId) {
        return mApi.agreeFriends(getRequestBody(new AgreeFriendsRequest(friendId)));
    }

    public Observable<DeleteFriendResponse> deleteFriend(String friendId) {
        return mApi.deleteFriend(getRequestBody(new DeleteFriendRequest(friendId)));
    }

    public Observable<SetFriendDisplayNameResponse> setFriendDisplayName(String friendId, String displayName) {
        return mApi.setFriendDisplayName(getRequestBody(new SetFriendDisplayNameRequest(friendId, displayName)));
    }

    public Observable<GetBlackListResponse> getBlackList() {
        return mApi.getBlackList();
    }

    public Observable<AddToBlackListResponse> addToBlackList(String friendId) {
        return mApi.addToBlackList(getRequestBody(new AddToBlackListRequest(friendId)));
    }

    public Observable<RemoveFromBlackListResponse> removeFromBlackList(String friendId) {
        return mApi.removeFromBlackList(getRequestBody(new RemoveFromBlacklistRequest(friendId)));
    }


    //群组
    public Observable<CreateGroupResponse> createGroup(String name, List<String> memberIds) {
        return mApi.createGroup(getRequestBody(new CreateGroupRequest(name, memberIds)));
    }

    public Observable<SetGroupPortraitResponse> setGroupPortrait(String groupId, String portraitUri) {
        return mApi.setGroupPortrait(getRequestBody(new SetGroupPortraitRequest(groupId, portraitUri)));
    }

    public Observable<GetGroupResponse> getGroups() {
        return mApi.getGroups();
    }

    public Observable<GetGroupInfoResponse> getGroupInfo(String groupId) {
        return mApi.getGroupInfo(groupId);
    }

    public Observable<GetGroupMemberResponse> getGroupMember(String groupId) {
        return mApi.getGroupMember(groupId);
    }

    public Observable<AddGroupMemberResponse> addGroupMember(String groupId, List<String> memberIds) {
        return mApi.addGroupMember(getRequestBody(new AddGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<DeleteGroupMemberResponse> deleGroupMember(String groupId, List<String> memberIds) {
        return mApi.deleGroupMember(getRequestBody(new DeleteGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<SetGroupNameResponse> setGroupName(String groupId, String name) {
        return mApi.setGroupName(getRequestBody(new SetGroupNameRequest(groupId, name)));
    }

    public Observable<QuitGroupResponse> quitGroup(String groupId) {
        return mApi.quitGroup(getRequestBody(new QuitGroupRequest(groupId)));
    }

    public Observable<QuitGroupResponse> dissmissGroup(String groupId) {
        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
    }
//    public Observable<DismissGroupResponse> dissmissGroup(String groupId) {
//        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
//    }

    public Observable<SetGroupDisplayNameResponse> setGroupDisplayName(String groupId, String displayName) {
        return mApi.setGroupDisplayName(getRequestBody(new SetGroupDisplayNameRequest(groupId, displayName)));
    }

    public Observable<JoinGroupResponse> JoinGroup(String groupId) {
        return mApi.JoinGroup(getRequestBody(new JoinGroupRequest(groupId)));
    }

    public Observable<DefaultConversationResponse> getDefaultConversation() {
        return mApi.getDefaultConversation();
    }

    public Observable<GetUserInfosResponse> getUserInfos(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append("id=");
            sb.append(s);
            sb.append("&");
        }
        String stringRequest = sb.substring(0, sb.length() - 1);
        return mApi.getUserInfos(stringRequest);
    }

    //public Observable<GetBtDeviceInfoByIDResponse> getBtDeviceInfoByID(String userid) {
    //    return mApi.getBtDeviceInfoByID(userid);
    //}

    //其他
    public Observable<QiNiuTokenResponse> getQiNiuToken() {
        return mApi.getQiNiuToken();
    }

    public Observable<VersionResponse> getClientVersion() {
        return mApi.getClientVersion();
    }

    public Observable<SyncTotalDataResponse> syncTotalData(String version) {
        return mApi.syncTotalData(version);
    }



}