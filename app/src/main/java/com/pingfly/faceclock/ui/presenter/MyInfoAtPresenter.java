package com.pingfly.faceclock.ui.presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lqr.imagepicker.bean.ImageItem;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.api.ApiRetrofit;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.db.DBManager;

import com.pingfly.faceclock.db.model.Friend;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.model.response.QiNiuTokenResponse;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IMyInfoAtView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;
import com.qiniu.android.storage.UploadManager;


import java.io.File;

import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MyInfoAtPresenter extends BasePresenter<IMyInfoAtView> {

    //private BluetoothUtils mBluetooth;
    //private boolean isBluetoothConnnect;

    public UserInfo mUserInfo;          // 融云UserInfo
    private UploadManager mUploadManager;   // 七牛云UploadManager

    public MyInfoAtPresenter(BaseActivity context) {
        super(context);
    }


    public void loadUserInfo() {    // 加载用户信息
        mUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (mUserInfo != null) {
            Glide.with(mContext).load(mUserInfo.getPortraitUri()).apply(RequestOptions.centerCropTransform()).into(getView().getIvHeader());
            getView().getOivName().setRightText(mUserInfo.getName());
            getView().getOivAccount().setRightText(mUserInfo.getUserId());
        }
    }


    public void setPortrait(ImageItem imageItem) {
        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        //上传头像
        ApiRetrofit.getInstance().getQiNiuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiNiuTokenResponse -> {
                    if (qiNiuTokenResponse != null && qiNiuTokenResponse.getCode() == 200) {
                        if (mUploadManager == null)
                            mUploadManager = new UploadManager();
                        File imageFile = new File(imageItem.path);
                        QiNiuTokenResponse.ResultEntity result = qiNiuTokenResponse.getResult();
                        String domain = result.getDomain();
                        String token = result.getToken();
                        // 将头像图片文件上传到七牛
                        mUploadManager.put(imageFile, null, token, (s, responseInfo, jsonObject) -> {
                            if (responseInfo.isOK()) {
                                String key = jsonObject.optString("key");
                                String imageUrl = "http://" + domain + "/" + key;
                                //修改自己服务器头像数据
                                ApiRetrofit.getInstance().setPortrait(imageUrl)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(setPortraitResponse -> {
                                            if (setPortraitResponse != null && setPortraitResponse.getCode() == 200) {
                                                Friend user = DBManager.getInstance().getFriendById(UserCache.getId());
                                                if (user != null) {
                                                    user.setPortraitUri(imageUrl);
                                                    DBManager.getInstance().saveOrUpdateFriend(user);
                                                    //DBManager.getInstance().updateGroupMemberPortraitUri(UserCache.getId(), imageUrl);
                                                    Glide.with(mContext).load(user.getPortraitUri()).apply(RequestOptions.centerCropTransform()).into(getView().getIvHeader());
                                                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
                                                    //BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                                                    //BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_GROUP);
                                                    UIUtils.showToast(UIUtils.getString(R.string.set_success));
                                                }
                                                mContext.hideWaitingDialog();
                                            } else {
                                                uploadError(null);
                                            }
                                        }, this::uploadError);
                            } else {
                                uploadError(null);
                            }
                        }, null);
                    } else {
                        uploadError(null);
                    }
                }, this::uploadError);
    }


    private void uploadError(Throwable throwable) {
        if (throwable != null)
            LogUtils.sf(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(R.string.set_fail));
    }

}