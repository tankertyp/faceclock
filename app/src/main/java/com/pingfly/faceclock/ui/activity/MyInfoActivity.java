package com.pingfly.faceclock.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;

//
import com.pingfly.faceclock.R;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.optionitemview.OptionItemView;

import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.presenter.MyInfoAtPresenter;
import com.pingfly.faceclock.ui.view.IMyInfoAtView;
import com.pingfly.faceclock.util.UIUtils;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @描述 我的个人信息
 */
public class MyInfoActivity extends BaseActivity<IMyInfoAtView, MyInfoAtPresenter>
        implements IMyInfoAtView {

    public static final int REQUEST_IMAGE_PICKER = 1000;  // 选择图片的请求码

    @BindView(R.id.llHeader)
    LinearLayout mLlHeader;
    @BindView(R.id.ivHeader)
    ImageView mIvHeader;
    @BindView(R.id.oivName)
    OptionItemView mOivName;
    @BindView(R.id.oivAccount)
    OptionItemView mOivAccount;
    @BindView(R.id.oivPassword)
    OptionItemView oivPassword;



    @Override
    public void init() {
        super.init();
        registerBR();
    }

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.my_info));
    }


    @Override
    public void initData() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void initListener() {
        mIvHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MyInfoActivity.this, ShowBigImageActivity.class);
            intent.putExtra("url", mPresenter.mUserInfo.getPortraitUri().toString());
            jumpToActivity(intent);
        });
        // ImageGridActivity中有拍照和图片两种模式
        mLlHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGridActivity.class);

            // 当我们在第一个Activity打开第二个Activity时，第二个Activity关闭并想返回数据给第一个Activity时，
            // 我们就要重写onActivityResult(int requestCode, int resultCode, Intent data)这个方法.
            startActivityForResult(intent, REQUEST_IMAGE_PICKER);  // 请求码的值是根据业务需要由自已设定，用于标识请求来源。
        });

        mOivName.setOnClickListener(v -> jumpToActivity(ResetNameActivity.class));
        //mOivPhone.setOnClickListener(v -> jumpToActivity(ResetPhoneActivity.class));
        oivPassword.setOnClickListener(v ->jumpToActivity(ChangePasswordActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 当第二个Activity关闭时，返回第一个Activity，
        // 在第一个Activity中重写onActivityResult方法，数据可以从data中取出
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {     // 图片列表中选择一张作为头像
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            ImageItem imageItem = images.get(0);
                            mPresenter.setPortrait(imageItem);
                        }
                    }
                }
        }
    }


    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.CHANGE_INFO_FOR_RESET_NAME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadUserInfo();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.CHANGE_INFO_FOR_RESET_NAME);
    }

    @Override
    protected MyInfoAtPresenter createPresenter() {
        return new MyInfoAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_my_info;
    }

    @Override
    public ImageView getIvHeader() {
        return mIvHeader;
    }

    @Override
    public OptionItemView getOivName() {
        return mOivName;
    }

    @Override
    public OptionItemView getOivAccount() {
        return mOivAccount;
    }

}
