package com.pingfly.faceclock.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.NetworkUtils;
import com.pingfly.faceclock.util.UIUtils;

import butterknife.BindView;

public class MessageActivity extends BaseActivity {


    @BindView(R.id.ivNoMessage)
    ImageView mIvNoMessage;
    @BindView(R.id.ivNoNetwork)
    ImageView mIvNoNetwork;

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.message));
        if(NetworkUtils.isNetworkAvailable(MessageActivity.this)) {
            // 当前有可用网络
            mIvNoMessage.setVisibility(View.VISIBLE);
            mIvNoNetwork.setVisibility(View.GONE);
        } else {
            // 当前没有可用网络
            mIvNoMessage.setVisibility(View.GONE);
            mIvNoNetwork.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_message;
    }


}
