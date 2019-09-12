package com.pingfly.faceclock.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BaseFragmentPresenter;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.PermissionsUtil;

import java.util.List;
import java.util.Objects;



public class WelcomeActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mContext = WelcomeActivity.this;
        initView();
    }

    public void initView() {
        View view = findViewById(R.id.rl_app_start);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(800);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                PermissionsUtil.checkAndRequestPermissions(WelcomeActivity.this, new PermissionsUtil.PermissionCallbacks() {
                    @Override
                    public void onPermissionsGranted() {
                        toLoginActivity();
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode, List perms) {

                    }

                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

    }

    /**
     * 跳转至LoginActivity
     */
    private void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionsUtil.REQUEST_STATUS_CODE) {

            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {//读写权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意
                    PermissionsUtil.checkAndRequestPermissions(this, new PermissionsUtil.PermissionCallbacks() {
                        @Override
                        public void onPermissionsGranted() {
                            toLoginActivity();
                        }

                        @Override
                        public void onPermissionsDenied(int requestCode, List perms) {

                        }

                    });//请求
                } else {//不同意
                    createLoadedAlertDialog("在设置-应用-"+ getString(R.string.app_name) +"-权限中开启存储空间权限，以正常使用App功能");
                }
            }

            if (permissions[0].equals(Manifest.permission.CALL_PHONE)) {//电话权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意
                    PermissionsUtil.checkAndRequestPermissions(this, new PermissionsUtil.PermissionCallbacks() {
                        @Override
                        public void onPermissionsGranted() {
                            toLoginActivity();
                        }

                        @Override
                        public void onPermissionsDenied(int requestCode, List perms) {

                        }

                    });
                } else {//不同意
                    createLoadedAlertDialog("在设置-应用-" + getString(R.string.app_name) + "-权限中开启电话权限，以正常使用App功能");
                }
            }

            if (permissions[0].equals(Manifest.permission.CAMERA)) {//电话权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意
                    //所有权限均获取
                    toLoginActivity();
                } else {//不同意
                    createLoadedAlertDialog("在设置-应用-"+ getString(R.string.app_name) +"-权限中开启照相机权限，以正常使用App功能");
                }
            }
        }

    }


    /**
     * 去设置dialog
     */
    public void createLoadedAlertDialog(String title) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();

        if (!dialog.isShowing()) {
            dialog.show();
        }
        dialog.setCancelable(true);
        final Window window = dialog.getWindow();

        Objects.requireNonNull(window).setContentView(R.layout.alert_dialog);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        TextView titleTv = (TextView) window.findViewById(R.id.title_tv);//内容
        TextView titleNoticeTv = (TextView) window.findViewById(R.id.title_notice_tv);//标题
        titleNoticeTv.setText("权限申请");
        titleTv.setText(title);
        TextView cancelTv = (TextView) window.findViewById(R.id.cancel_tv); // 取消点击
        TextView okTv = (TextView) window.findViewById(R.id.ok_tv); // 确认点击

        cancelTv.setText("取消");
        okTv.setText("去设置");

        // #1 取消键
        cancelTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
        // #2 确认键
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PermissionsUtil.REQUEST_PERMISSION_SETTING);
                finish();
                dialog.cancel();
            }

        });
    }

}
