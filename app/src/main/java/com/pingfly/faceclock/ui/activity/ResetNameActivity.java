package com.pingfly.faceclock.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.api.ApiRetrofit;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.db.model.Friend;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.BindView;

import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ResetNameActivity extends BaseActivity {

    @BindView(R.id.llToolbarTitle)
    AutoLinearLayout mLlToolbarTitle;
    @BindView(R.id.btnToolbarSend)
    Button mBtnToolbarSend;
    @BindView(R.id.etName)
    EditText mEtName;

    @Override
    public void initView() {

        setToolbarTitle(UIUtils.getString(R.string.change_name));
        //mLlToolbarTitle.setVisibility(View.GONE);
        mBtnToolbarSend.setText(UIUtils.getString(R.string.save));
        mBtnToolbarSend.setVisibility(View.VISIBLE);    // 进入修改昵称界面后，保存按钮默认为灰色
        UserInfo userInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (userInfo != null)
            mEtName.setText(userInfo.getName());
        mEtName.setSelection(mEtName.getText().toString().trim().length());
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(v -> resetName());
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 如果昵称已经存在，在未编辑状态，“保存”按钮显示为灰色
                if (mEtName.getText().toString().trim().length() > 0) {
                    mBtnToolbarSend.setEnabled(true);
                } else {    // 否则颜色加深，表示进入了编辑状态
                    mBtnToolbarSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void resetName() {
        showWaitingDialog(UIUtils.getString(R.string.please_wait));
        String nickName = mEtName.getText().toString().trim();
        ApiRetrofit.getInstance().setName(nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(setNameResponse -> {
                    hideWaitingDialog();
                    if (setNameResponse.getCode() == 200) {      // 修改昵称响应
                        Friend user = DBManager.getInstance().getFriendById(UserCache.getId());
                        if (user != null) {
                            user.setName(nickName);
                            user.setDisplayName(nickName);
                            DBManager.getInstance().saveOrUpdateFriend(user);
                            BroadcastManager.getInstance(ResetNameActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
                            BroadcastManager.getInstance(ResetNameActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_RESET_NAME);
                        }
                        finish();
                    }
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        hideWaitingDialog();
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_reset_name;
    }
}


