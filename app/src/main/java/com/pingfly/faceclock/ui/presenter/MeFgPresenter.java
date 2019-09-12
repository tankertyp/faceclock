package com.pingfly.faceclock.ui.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.api.ApiRetrofit;

import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.db.model.Friend;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.model.response.GetUserInfoByIdResponse;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IMeFgView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import io.rong.imlib.model.UserInfo;    // 用户信息实体类，用来容纳和存储用户信息。
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MeFgPresenter extends BasePresenter<IMeFgView> {
    private UserInfo mUserInfo;
    private boolean isFirst = true;

    public MeFgPresenter(BaseActivity context) {
        super(context);
    }


    // 获取用户的账号，昵称，头像信息进行显示
    public void loadUserInfo() {
        mUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (mUserInfo == null || isFirst) {
            isFirst = false;

            ApiRetrofit.getInstance().getUserInfoById(UserCache.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUserInfoByIdResponse -> {
                        if (getUserInfoByIdResponse != null && getUserInfoByIdResponse.getCode() == 200) {
                            GetUserInfoByIdResponse.ResultEntity result = getUserInfoByIdResponse.getResult();

                            mUserInfo = new UserInfo(UserCache.getId(), result.getname(), Uri.parse(result.getPortraitUri()));
                            if (TextUtils.isEmpty(mUserInfo.getPortraitUri().toString())) {
                                mUserInfo.setPortraitUri(Uri.parse(DBManager.getInstance().getPortraitUri(mUserInfo)));
                            }

                            DBManager.getInstance().saveOrUpdateFriend(new Friend(mUserInfo.getUserId(), mUserInfo.getName(), mUserInfo.getPortraitUri().toString()));
                            fillView();
                        }
                    }, this::loadError);
        } else {
            fillView();
        }
    }

    /**
     * 更新用户数据
     */
    public void refreshUserInfo() {
        //UserInfo userInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        UserInfo userInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (userInfo == null) {
            loadUserInfo();
        } else {
            mUserInfo = userInfo;
        }
    }


    public void fillView() {
        if (mUserInfo != null) {
            // 加载头像
            Glide.with(mContext).load(mUserInfo.getPortraitUri()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(getView().getIvHeader());
            //
            getView().getTvAccount().setText(UIUtils.getString(R.string.my_account, mUserInfo.getUserId()));
            getView().getTvName().setText(mUserInfo.getName());
        }
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
