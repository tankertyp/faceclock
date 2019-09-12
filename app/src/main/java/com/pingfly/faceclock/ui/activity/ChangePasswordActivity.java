package com.pingfly.faceclock.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.pingfly.faceclock.R;
import com.pingfly.faceclock.api.ApiRetrofit;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.db.model.Friend;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.model.response.ChangePasswordResponse;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.MD5Utils;
import com.pingfly.faceclock.util.UIUtils;


import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.top_bar)
    TextView main_title;
    @BindView(R.id.btnToolbarSend)
    Button mBtnToolbarSend;

    @BindView(R.id.etOldPassword)
    EditText mEtOldPassword;
    @BindView(R.id.vLineOldPassword)
    View mVLineOldPassword;

    @BindView(R.id.etNewPassword)
    EditText mEtNewPassword;
    @BindView(R.id.vLineNewPassword)
    View mVLineNewPassword;

    @BindView(R.id.etNewPasswordAgain)
    EditText mEtNewPasswordAgain;
    @BindView(R.id.vLineNewPasswordAgain)
    View mVLineNewPasswordAgain;

    private String userName;

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.reset_password));
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnToolbarSend.setEnabled(canResetPassword());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void initListener() {
        mEtOldPassword.addTextChangedListener(watcher);
        mEtNewPassword.addTextChangedListener(watcher);
        mEtNewPasswordAgain.addTextChangedListener(watcher);

        mEtOldPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineOldPassword.setBackgroundColor(UIUtils.getColor(R.color.green0));
            } else {
                mVLineOldPassword.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });

        mEtNewPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineNewPassword.setBackgroundColor(UIUtils.getColor(R.color.green0));
            } else {
                mVLineNewPassword.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtNewPasswordAgain.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineNewPasswordAgain.setBackgroundColor(UIUtils.getColor(R.color.green0));
            } else {
                mVLineNewPasswordAgain.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });

        mBtnToolbarSend.setOnClickListener(v -> changePassword());
    }


    public void changePassword() {

        String oldPassword = mEtOldPassword.getText().toString().trim();
        String newPassword = mEtNewPassword.getText().toString().trim();
        String newPasswordAgain = mEtNewPasswordAgain.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)){
            //Toast.makeText(this,"请输入旧密码",Toast.LENGTH_SHORT).show();
            UIUtils.showToast(UIUtils.getString(R.string.please_input_old_password));
            return;
        }else if (MD5Utils.MD5(newPassword).equals(readPassword())){
            //Toast.makeText(this,"输入的新密码与原始密码不能一致",Toast.LENGTH_SHORT).show();
            UIUtils.showToast(UIUtils.getString(R.string.original_password_not_same));
            return;
        }else if (TextUtils.isEmpty(newPassword)){
            //Toast.makeText(this,"请输入新密码",Toast.LENGTH_SHORT).show();
            UIUtils.showToast(UIUtils.getString(R.string.please_input_new_password));
            return;
        }else if (TextUtils.isEmpty(newPasswordAgain)){
            //Toast.makeText(this,"请再次输入新密码",Toast.LENGTH_SHORT).show();
            UIUtils.showToast(UIUtils.getString(R.string.please_input_new_password_again));
        }else if (!newPassword.equals(newPasswordAgain)){
            //Toast.makeText(this,"再次输入的新密码不一致",Toast.LENGTH_SHORT).show();
            UIUtils.showToast(UIUtils.getString(R.string.new_password_not_same));
            return;
        }else {
            showWaitingDialog(UIUtils.getString(R.string.please_wait));
            ApiRetrofit.getInstance().changePassword(oldPassword,newPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(setNameResponse -> {
                        hideWaitingDialog();
                        if (ChangePasswordResponse.getCode() == 200) {  // 如果接到改变密码响应成功
                            // Friend friend = DBManager.getInstance().getFriendById(UserCache.getId());
                            Friend user = DBManager.getInstance().getFriendById(UserCache.getId());
                            if (user != null) {
                                DBManager.getInstance().saveOrUpdateFriend(user);
                                BroadcastManager.getInstance(ChangePasswordActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
                                BroadcastManager.getInstance(ChangePasswordActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_CHANGE_PASSWORD);
                            }
                            finish();
                        }
                    }, this::changePasswordError);
        }
    }

    // 获取用户自己的密码
    private String readPassword() {

        Friend user = DBManager.getInstance().getFriendById(UserCache.getId());
        //return user.getUserPwd();
        return "123456";
    }




    private void changePasswordError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }


    private boolean canResetPassword() {
        int oldPasswordLength = mEtOldPassword.getText().toString().trim().length();
        int  newPasswordLength= mEtNewPassword.getText().toString().trim().length();
        int newPasswordAgainLength = mEtNewPasswordAgain.getText().toString().trim().length();
        if ( oldPasswordLength > 0 && newPasswordLength > 0 && newPasswordAgainLength > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_change_password;
    }

}
