package com.pingfly.faceclock.app;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONException;
import com.pingfly.faceclock.manager.JsonManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.loader.ImageLoader;
import com.lqr.imagepicker.view.CropImageView;
import com.pingfly.faceclock.app.base.BaseApp;
import com.houxya.bthelper.BtHelper;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.db.model.Friend;
import com.pingfly.faceclock.db.model.Groups;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.model.data.GroupNotificationMessageData;
import com.pingfly.faceclock.model.message.DeleteContactMessage;
import com.pingfly.faceclock.model.response.ContactNotificationMessageData;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.PinyinUtils;
import com.pingfly.faceclock.util.UIUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.GroupNotificationMessage;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  @描述 BaseApp的拓展，用于设置其他第三方的初始化
 */
public class MyApp extends BaseApp implements RongIMClient.OnReceiveMessageListener{

    public static String cacheDir = "";

    @Override
    public void onCreate() {
        super.onCreate();
        //initChannel();
        LitePal.initialize(this);
        //初始化融云
        //initRongCloud();
        //初始化仿微信控件ImagePicker
        initImagePicker();
        //ShareSDK.initSDK(getContext());

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }

        // First，Init BtHelper in your Application
        BtHelper.init(this);


    }

    @Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            if (contactNotificationMessage.getOperation().equals(ContactNotificationMessage.CONTACT_OPERATION_REQUEST)) {
                //对方发来好友邀请
                BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_RED_DOT);
            } else {
                //对方同意我的好友请求
                ContactNotificationMessageData c = null;
                try {
                    c = JsonManager.jsonToBean(contactNotificationMessage.getExtra(), ContactNotificationMessageData.class);
                } catch (HttpException | JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                if (c != null) {
                    if (DBManager.getInstance().isMyFriend(contactNotificationMessage.getSourceUserId()))
                        return false;
                    DBManager.getInstance().saveOrUpdateFriend(
                            new Friend(contactNotificationMessage.getSourceUserId(),
                                    c.getSourceUserNickname(),
                                    null, c.getSourceUserNickname(), null, null,
                                    null, null,
                                    PinyinUtils.getPinyin(c.getSourceUserNickname()),
                                    PinyinUtils.getPinyin(c.getSourceUserNickname())
                            )
                    );
                    BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
                    BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_RED_DOT);
                }
            }
        } else if (messageContent instanceof DeleteContactMessage) {
            DeleteContactMessage deleteContactMessage = (DeleteContactMessage) messageContent;
            String contact_id = deleteContactMessage.getContact_id();
            RongIMClient.getInstance().getConversation(Conversation.ConversationType.PRIVATE, contact_id, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    RongIMClient.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, contact_id, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            RongIMClient.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, contact_id, null);
                            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
            DBManager.getInstance().deleteFriendById(contact_id);
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
        } else if (messageContent instanceof GroupNotificationMessage) {
            GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) messageContent;
            String groupId = message.getTargetId();
            GroupNotificationMessageData data = null;
            try {
                String curUserId = UserCache.getId();
                try {
                    data = jsonToBean(groupNotificationMessage.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (groupNotificationMessage.getOperation()) {
                    case GroupNotificationMessage.GROUP_OPERATION_CREATE:
                        DBManager.getInstance().getGroups(groupId);
                        DBManager.getInstance().getGroupMember(groupId);
                        break;
                    case GroupNotificationMessage.GROUP_OPERATION_DISMISS:
                        handleGroupDismiss(groupId);
                        break;
                    case GroupNotificationMessage.GROUP_OPERATION_KICKED:
                        if (data != null) {
                            List<String> memberIdList = data.getTargetUserIds();
                            if (memberIdList != null) {
                                for (String userId : memberIdList) {
                                    if (curUserId.equals(userId)) {
                                        RongIMClient.getInstance().removeConversation(Conversation.ConversationType.GROUP, message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                LogUtils.sf("Conversation remove successfully.");
                                            }

                                            @Override
                                            public void onError(RongIMClient.ErrorCode e) {

                                            }
                                        });
                                    }
                                }
                            }
                            List<String> kickedUserIDs = data.getTargetUserIds();
                            DBManager.getInstance().deleteGroupMembers(groupId, kickedUserIDs);
                            //因为操作存在异步，故不在这里发送广播
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        }
                        break;
                    case GroupNotificationMessage.GROUP_OPERATION_ADD:
                        DBManager.getInstance().getGroups(groupId);
                        DBManager.getInstance().getGroupMember(groupId);
                        //因为操作存在异步，故不在这里发送广播
//                    BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
                        break;
                    case GroupNotificationMessage.GROUP_OPERATION_QUIT:
                        if (data != null) {
                            List<String> quitUserIDs = data.getTargetUserIds();
                            DBManager.getInstance().deleteGroupMembers(groupId, quitUserIDs);
                            //因为操作存在异步，故不在这里发送广播
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        }
                        break;
                    case GroupNotificationMessage.GROUP_OPERATION_RENAME:
                        if (data != null) {
                            String targetGroupName = data.getTargetGroupName();
                            DBManager.getInstance().updateGroupsName(groupId, targetGroupName);
                            //更新群名
                            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CURRENT_SESSION_NAME);
                            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
        } else {
            //TODO:还有其他类型的信息
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CURRENT_SESSION, message);
        }
        return false;
    }

    private void handleGroupDismiss(final String groupId) {
        RongIMClient.getInstance().getConversation(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                RongIMClient.getInstance().clearMessages(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        RongIMClient.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                DBManager.getInstance().deleteGroup(new Groups(groupId));
                                DBManager.getInstance().deleteGroupMembersByGroupId(groupId);
                                BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                                BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.GROUP_LIST_UPDATE);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private GroupNotificationMessageData jsonToBean(String data) {
        GroupNotificationMessageData dataEntity = new GroupNotificationMessageData();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("operatorNickname")) {
                dataEntity.setOperatorNickname(jsonObject.getString("operatorNickname"));
            }
            if (jsonObject.has("targetGroupName")) {
                dataEntity.setTargetGroupName(jsonObject.getString("targetGroupName"));
            }
            if (jsonObject.has("timestamp")) {
                dataEntity.setTimestamp(jsonObject.getLong("timestamp"));
            }
            if (jsonObject.has("targetUserIds")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserIds");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserIds().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("targetUserDisplayNames")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserDisplayNames");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserDisplayNames().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("oldCreatorId")) {
                dataEntity.setOldCreatorId(jsonObject.getString("oldCreatorId"));
            }
            if (jsonObject.has("oldCreatorName")) {
                dataEntity.setOldCreatorName(jsonObject.getString("oldCreatorName"));
            }
            if (jsonObject.has("newCreatorId")) {
                dataEntity.setNewCreatorId(jsonObject.getString("newCreatorId"));
            }
            if (jsonObject.has("newCreatorName")) {
                dataEntity.setNewCreatorName(jsonObject.getString("newCreatorName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataEntity;
    }

    private void initRongCloud() {
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIMClient.init(this);
        }

        //监听接收到的消息
        RongIMClient.setOnReceiveMessageListener(this);
        try {
            //RongIMClient.registerMessageType(RedPacketMessage.class);
            RongIMClient.registerMessageType(DeleteContactMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化仿微信控件ImagePicker
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                //Glide.with(getContext()).load(Uri.parse("file://" + path).toString()).centerCrop().into(imageView);
                Glide.with(getContext()).load(Uri.parse("file://" + path).toString()).apply(RequestOptions.centerCropTransform()).into(imageView);
            }

            @Override
            public void clearMemoryCache() {

            }
        });   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : Objects.requireNonNull(activityManager)
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
