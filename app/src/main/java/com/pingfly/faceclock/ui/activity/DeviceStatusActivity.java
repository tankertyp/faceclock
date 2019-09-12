package com.pingfly.faceclock.ui.activity;

import android.view.View;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.zhy.autolayout.AutoFrameLayout;

import butterknife.BindView;

public class DeviceStatusActivity extends BaseActivity  {


    @BindView(R.id.flToolbar)
    AutoFrameLayout toolBar;

    @Override
    public void initView() {
        toolBar.setVisibility(View.GONE);
        //mIbAddMenu.setVisibility(View.GONE);
    }



    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_device_status;
    }

}
