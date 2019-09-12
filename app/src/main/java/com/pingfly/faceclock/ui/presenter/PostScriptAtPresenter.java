package com.pingfly.faceclock.ui.presenter;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.api.ApiRetrofit;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IPostScriptAtView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PostScriptAtPresenter extends BasePresenter<IPostScriptAtView> {

    public PostScriptAtPresenter(BaseActivity context) {
        super(context);
    }

    public void addFriend(String userId) {
        String msg = getView().getEtMsg().getText().toString().trim();
        ApiRetrofit.getInstance().sendFriendInvitation(userId, msg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendInvitationResponse -> {
                    if (friendInvitationResponse.getCode() == 200) {
                        UIUtils.showToast(UIUtils.getString(R.string.request_sent_success));
                        mContext.finish();
                    } else {
                        UIUtils.showToast(UIUtils.getString(R.string.request_sent_fail));
                    }
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }
}
