package com.pingfly.faceclock.ui.activity;


import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.presenter.RegisterAtPresenter;
import com.pingfly.faceclock.ui.view.IRegisterAtView;
import com.pingfly.faceclock.util.UIUtils;

import butterknife.BindView;


/**
 * 注册Activity 可以让你在App 中创建一个用户，通常会在登录Activity 中显示（注册的）链接。
 */

//import rx.android.schedulers.AndroidSchedulers;


public class RegisterActivity extends  BaseActivity<IRegisterAtView, RegisterAtPresenter> implements IRegisterAtView {

    @BindView(R.id.etNick)
    EditText mEtNick;
    @BindView(R.id.vLineNick)
    View mVLineNick;

    @BindView(R.id.etPhone)
    EditText mEtPhone;
    @BindView(R.id.vLinePhone)
    View mVLinePhone;

    @BindView(R.id.etEmail)
    EditText mEtEmail;
    @BindView(R.id.vLineEmail)
    View mVLineEmail;

    @BindView(R.id.etPwd)
    EditText mEtPwd;
    @BindView(R.id.ivSeePwd)
    ImageView mIvSeePwd;
    @BindView(R.id.vLinePwd)
    View mVLinePwd;

    @BindView(R.id.etVerifyCode)
    EditText mEtVerifyCode;
    @BindView(R.id.btnSendCode)
    Button mBtnSendCode;
    @BindView(R.id.vLineVertifyCode)
    View mVLineVertifyCode;

    @BindView(R.id.btnRegister)
    Button mBtnRegister;

    @Override
    public void initView() {
        setToolbarTitle(getString(R.string.register));
    }


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnRegister.setEnabled(canRegister());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    @Override
    public void initListener() {
        mEtNick.addTextChangedListener(watcher);
        mEtPwd.addTextChangedListener(watcher);
        mEtPhone.addTextChangedListener(watcher);
        mEtEmail.addTextChangedListener(watcher);
        mEtVerifyCode.addTextChangedListener(watcher);

        mEtNick.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineNick.setBackgroundColor(UIUtils.getColor(R.color.main_blue));
            } else {
                mVLineNick.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.main_blue));
            } else {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePhone.setBackgroundColor(UIUtils.getColor(R.color.main_blue));
            } else {
                mVLinePhone.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineEmail.setBackgroundColor(UIUtils.getColor(R.color.main_blue));
            } else {
                mVLineEmail.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        mEtVerifyCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineVertifyCode.setBackgroundColor(UIUtils.getColor(R.color.main_blue));
            } else {
                mVLineVertifyCode.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });

        mIvSeePwd.setOnClickListener(v -> {

            if (mEtPwd.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }

            mEtPwd.setSelection(mEtPwd.getText().toString().trim().length());
        });

        // 发送验证码按钮
        mBtnSendCode.setOnClickListener(v -> {
            if (mBtnSendCode.isEnabled()) {
                mPresenter.sendCode();
            }
        });

        mBtnRegister.setOnClickListener(v -> mPresenter.register());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    private boolean canRegister() {
        int nameLength = mEtNick.getText().toString().trim().length();
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int phoneLength = mEtPhone.getText().toString().trim().length();
        int emailLength = mEtEmail.getText().toString().trim().length();
        int codeLength = mEtVerifyCode.getText().toString().trim().length();
        return nameLength > 0 && pwdLength > 0 && phoneLength > 0 && emailLength > 0 && codeLength > 0;
    }

    @Override
    protected RegisterAtPresenter createPresenter() {
        return new RegisterAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    public EditText getEtNickName() {
        return mEtNick;
    }


    @Override
    public EditText getEtEmail() {
        return mEtEmail;
    }

    @Override
    public EditText getEtPhone() {
        return mEtPhone;
    }

    @Override
    public EditText getEtPwd() {
        return mEtPwd;
    }

    @Override
    public EditText getEtVerifyCode() {
        return mEtVerifyCode;
    }

    @Override
    public Button getBtnSendCode() {
        return mBtnSendCode;
    }

}

