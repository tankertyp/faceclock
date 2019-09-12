package com.pingfly.faceclock.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.zhy.autolayout.AutoFrameLayout;

import butterknife.BindView;

import kr.co.namee.permissiongen.PermissionGen;

public class SplashActivity  extends BaseActivity {

    @BindView(R.id.flToolbar)
    AutoFrameLayout toolBar;
    @BindView(R.id.main_content)
    CoordinatorLayout mClMainContent;
    @BindView(R.id.ivFaceClock)
    ImageView mIvFaceClock;



    @Override
    public void init() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        //电话通讯录
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_PHONE_STATE,
                        //位置
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        //相机、麦克风
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.CAMERA,
                        //存储空间
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SETTINGS,
                        //网络状态
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        // 振动
                        Manifest.permission.VIBRATE

                )
                .request();
        if (!TextUtils.isEmpty(UserCache.getToken())) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            jumpToActivity(intent);
            finish();
        }
    }

    @Override
    public void initView() {
        toolBar.setVisibility(View.GONE);
        //AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        //alphaAnimation.setDuration(50);
        //mRlButton.startAnimation(alphaAnimation);

    }

    @Override
    public void initListener() {
        mClMainContent.setOnClickListener(v -> {
            jumpToActivity(LoginActivity.class);
            finish();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_splash;
    }



}
