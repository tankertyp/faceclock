package com.pingfly.faceclock.ui.presenter;

import android.text.TextUtils;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.api.ApiRetrofit;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.model.exception.ServerException;
import com.pingfly.faceclock.model.response.CheckEmailResponse;
import com.pingfly.faceclock.model.response.CheckPhoneResponse;
import com.pingfly.faceclock.model.response.LoginResponse;
import com.pingfly.faceclock.model.response.RegisterResponse;
import com.pingfly.faceclock.model.response.SendCodeByEmailResponse;
import com.pingfly.faceclock.model.response.SendCodeResponse;
import com.pingfly.faceclock.model.response.VerifyCodeResponse;
import com.pingfly.faceclock.ui.activity.LoginActivity;
import com.pingfly.faceclock.ui.activity.MainActivity;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IRegisterAtView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.RegularUtils;
import com.pingfly.faceclock.util.UIUtils;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RegisterAtPresenter extends BasePresenter<IRegisterAtView> {

    int time = 0;
    private Timer mTimer;
    private Subscription mSubscription;

    public RegisterAtPresenter(BaseActivity context) {
        super(context);
    }

    public void sendCode() {
        String phone = getView().getEtPhone().getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_not_empty));
            return;
        }

        if (!RegularUtils.isMobile(phone)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_format_error));
            return;
        }


        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));    // 显示“请稍等”对话框
        ApiRetrofit.getInstance().checkPhoneAvailable(AppConst.REGION, phone)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<CheckPhoneResponse, Observable<SendCodeResponse>>() {
                    @Override
                    public Observable<SendCodeResponse> call(CheckPhoneResponse checkPhoneResponse) {
                        int code = checkPhoneResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().sendCode(AppConst.REGION, phone);
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.phone_not_available)));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sendCodeResponse -> {
                    mContext.hideWaitingDialog();
                    int code = sendCodeResponse.getCode();
                    if (code == 200) {
                        changeSendCodeBtn();
                    } else {
                        sendCodeError(new ServerException(UIUtils.getString(R.string.send_code_error)));
                    }
                }, this::sendCodeError);
    }

    public void sendCodeByEmail() {
        String email = getView().getEtPhone().getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            UIUtils.showToast(UIUtils.getString(R.string.email_not_empty));
            return;
        }

        if (!RegularUtils.isEmail(email)) {
            UIUtils.showToast(UIUtils.getString(R.string.email_format_error));
            return;
        }

        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));    // 显示“请稍等”对话框
        ApiRetrofit.getInstance().checkEmailAvailable(email)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<CheckEmailResponse, Observable<SendCodeByEmailResponse>>() {
                    @Override
                    public Observable<SendCodeByEmailResponse> call(CheckEmailResponse checkEmailResponse) {
                        int code = checkEmailResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().sendCodeByEmail(email);
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.email_not_available)));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sendCodeByEmailResponse -> {
                    mContext.hideWaitingDialog();
                    int code = sendCodeByEmailResponse.getCode();
                    if (code == 200) {
                        changeSendCodeBtn();
                    } else {
                        sendCodeError(new ServerException(UIUtils.getString(R.string.send_code_error)));
                    }
                }, this::sendCodeError);
    }



    private void sendCodeError(Throwable throwable) {
        mContext.hideWaitingDialog();
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    private void changeSendCodeBtn() {
        //开始1分钟倒计时
        //每一秒执行一次Task
        mSubscription = Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
            time = 60;
            TimerTask mTask = new TimerTask() {
                @Override
                public void run() {
                    subscriber.onNext(--time);
                }
            };
            mTimer = new Timer();
            mTimer.schedule(mTask, 0, 1000);//每一秒执行一次Task
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    if (getView().getBtnSendCode() != null) {
                        if (time >= 0) {
                            getView().getBtnSendCode().setEnabled(false);
                            getView().getBtnSendCode().setText(time + "");
                        } else {
                            getView().getBtnSendCode().setEnabled(true);
                            getView().getBtnSendCode().setText(UIUtils.getString(R.string.send_code_btn_normal_tip));
                        }
                    } else {
                        mTimer.cancel();
                    }
                }, throwable -> LogUtils.sf(throwable.getLocalizedMessage()));
    }

    public void register() {
        String userName = getView().getEtPhone().getText().toString().trim();
        String name = getView().getEtNickName().getText().toString().trim();
        String phone = getView().getEtPhone().getText().toString().trim();
        String password = getView().getEtPwd().getText().toString().trim();

        String code = getView().getEtVerifyCode().getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_not_empty));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            UIUtils.showToast(UIUtils.getString(R.string.password_not_empty));
            return;
        }
        if (TextUtils.isEmpty(name)) {
            UIUtils.showToast(UIUtils.getString(R.string.name_not_empty));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            UIUtils.showToast(UIUtils.getString(R.string.vertify_code_not_empty));
            return;
        }

        ApiRetrofit.getInstance().verifyCode(AppConst.REGION, phone, code)
                .flatMap(new Func1<VerifyCodeResponse, Observable<RegisterResponse>>() {
                    @Override
                    public Observable<RegisterResponse> call(VerifyCodeResponse verifyCodeResponse) {
                        int code = verifyCodeResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().register(name, password, verifyCodeResponse.getResult().getVerification_token());
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.vertify_code_error) + code));
                        }
                    }
                })
                .flatMap(new Func1<RegisterResponse, Observable<LoginResponse>>() {
                    @Override
                    public Observable<LoginResponse> call(RegisterResponse registerResponse) {
                        int code = registerResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().login(AppConst.REGION, phone, password);
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.register_error) + code));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    int responseCode = loginResponse.getCode();      // 获得登录响应码
                    if (responseCode == 200) {
                        UserCache.save(loginResponse.getResult().getId(), phone, loginResponse.getResult().getToken());
                        mContext.jumpToActivityAndClearTask(MainActivity.class);
                        mContext.finish();
                    } else {
                        UIUtils.showToast(UIUtils.getString(R.string.login_error));
                        mContext.jumpToActivity(LoginActivity.class);
                    }
                }, this::registerError);
    }

    private void registerError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public void unsubscribe() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

}