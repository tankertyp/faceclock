package com.pingfly.faceclock.ui.presenter;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.activity.MainActivity;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.ILoginAtView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import static android.content.Context.MODE_PRIVATE;

public class LoginAtPresenter extends BasePresenter<ILoginAtView> {

    public LoginAtPresenter(BaseActivity context) {
        super(context);
    }

    public void login() {

        String mail= getView().getEtUserName().getText().toString().trim(); // mail作为账号
        String pwd = getView().getEtPwd().getText().toString().trim();  // 登录密码

        // 特定账户
        String your_mail = "yang_qianyi@qq.com";
        String your_pwd = "123456";

        if (TextUtils.isEmpty(mail)) {  // 账号(电话/邮箱)不能为空
            UIUtils.showToast(UIUtils.getString(R.string.email_not_empty));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {   // 密码不能为空
            UIUtils.showToast(UIUtils.getString(R.string.password_not_empty));
            return;
        }

        //mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        //mContext.hideWaitingDialog();
        if (mail.equals(your_mail) && pwd.equals(your_pwd)) {  // 成功

            input();//登录成功就把数据存起来

            mContext.jumpToActivityAndClearTask(MainActivity.class);
            mContext.finish();
        } else {
            //loginError(new ServerException(UIUtils.getString(R.string.login_error) + 1000));
            UIUtils.showToast(UIUtils.getString(R.string.login_fail));
        }


        /**
        ApiRetrofit.getInstance().login(AppConst.REGION, phone, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    int code = loginResponse.getCode();
                    mContext.hideWaitingDialog();
                    if (code == 200) {  // 成功
                        UserCache.save(loginResponse.getResult().getId(), phone, loginResponse.getResult().getToken());
                        mContext.jumpToActivityAndClearTask(MainActivity.class);
                        mContext.finish();
                    } else {
                        loginError(new ServerException(UIUtils.getString(R.string.login_error) + code));
                    }
                }, this::loginError);
                */
    }

    /**
     * 记住登录状态
     */
    private void input() {
        //第一个参数是文件名，第二个参数是模式（不明白可以去补习一下SharedPreferences的知识）
        SharedPreferences.Editor edit = mContext.getSharedPreferences("userinfo", MODE_PRIVATE).edit();

        edit.putString("username", getView().getEtUserName().getText().toString());
        edit.putString("password", getView().getEtPwd().getText().toString());

        edit.apply();

    }


    private void loginError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
    }
}