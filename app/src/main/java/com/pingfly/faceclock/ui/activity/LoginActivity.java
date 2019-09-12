package com.pingfly.faceclock.ui.activity;

import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.presenter.LoginAtPresenter;
import com.pingfly.faceclock.ui.view.ILoginAtView;
import com.zhy.autolayout.AutoFrameLayout;

import butterknife.BindView;

public class LoginActivity extends BaseActivity<ILoginAtView, LoginAtPresenter>
        implements ILoginAtView {

    @BindView(R.id.flToolbar)
    AutoFrameLayout toolBar;

    @BindView(R.id.etUserName)
    EditText mEtUsername;         //用户名编辑
    //@BindView(R.id.vLineUserName)
    //EditText mVLineUserName;

    @BindView(R.id.etPwd)
    EditText mEtPwd;              //密码编辑
    //@BindView(R.id.vLinePwd)
    //View mVLinePwd;

    @BindView(R.id.tvRegister)
    TextView mTvRegister;          // 注册账户
    @BindView(R.id.tvRetrievePwd)
    TextView mTvRetrievePwd;       // 忘记密码
    @BindView(R.id.btnLogin)
    Button mBtnLogin;              //登录按钮


    /**
     * 输入检查
     */
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mBtnLogin.setEnabled(canLogin());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void initView() {
        toolBar.setVisibility(View.GONE);
        //mIbAddMenu.setVisibility(View.GONE);
    }



    @Override
    public void initListener() {
        mEtUsername.addTextChangedListener(watcher);
        mEtPwd.addTextChangedListener(watcher);

        /**
        mEtUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLineUserName.setBackgroundColor(UIUtils.getColor(R.color.green0));
            } else {
                mVLineUserName.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });

        mEtPwd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.green0));
            } else {
                mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.line));
            }
        });
        */
        mBtnLogin.setOnClickListener(v -> mPresenter.login());
        mTvRegister.setOnClickListener(v -> jumpToActivity(RegisterActivity.class));

    }

    private boolean canLogin() {
        int pwdLength = mEtPwd.getText().toString().trim().length();
        int usernameLength = mEtUsername.getText().toString().trim().length();
        if ( usernameLength > 0 && pwdLength > 0 ) {
            return true;
        }
        return false;
    }

    @Override
    protected LoginAtPresenter createPresenter() {
        return new LoginAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_login;
    }


    @Override
    public EditText getEtUserName() {
        return mEtUsername;
    }

    @Override
    public EditText getEtPwd() {
        return mEtPwd;
    }

}
