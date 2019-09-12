package com.pingfly.faceclock.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqr.optionitemview.OptionItemView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.app.MyApp;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.ui.activity.InstructionActivity;
import com.pingfly.faceclock.ui.activity.LoginActivity;
import com.pingfly.faceclock.ui.activity.MainActivity;
import com.pingfly.faceclock.ui.activity.MessageActivity;
import com.pingfly.faceclock.ui.activity.MyInfoActivity;
import com.pingfly.faceclock.ui.base.BaseFragment;
import com.pingfly.faceclock.ui.presenter.MeFgPresenter;
import com.pingfly.faceclock.ui.view.IMeFgView;
import com.pingfly.faceclock.widget.CustomDialog;


// Butter Knife，专门为Android View设计的绑定注解，专业解决各种findViewById。

import java.util.Objects;

import butterknife.BindView;
import io.rong.imlib.RongIMClient;

public class MeFragment extends BaseFragment<IMeFgView, MeFgPresenter> implements IMeFgView {

    private View mExitView;

    @BindView(R.id.llMyInfo)
    LinearLayout mLlMyInfo;
    @BindView(R.id.ivHeader)
    ImageView mIvProfile;
    @BindView(R.id.tvName)
    TextView mTvName;
    @BindView(R.id.tvAccount)
    TextView mTvAccount;
    @BindView(R.id.oivInstruction)
    OptionItemView mOivInstruction;
    @BindView(R.id.oivMessage)
    OptionItemView mOivMessage;
    @BindView(R.id.oivLogout)
    OptionItemView mOivLogout;

   private CustomDialog mExitDialog;



    @Override
    public void init() {
        registerBR();
    }


    @Override
    public void initData() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initListener() {
        mLlMyInfo.setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivityAndClearTop(MyInfoActivity.class));
        //mOivSetting.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(SettingActivity.class));

        mOivLogout.setOnClickListener(v -> {
            if (mExitView == null) {
                //mExitView = View.inflate(this, R.layout.dialog_exit, null);
                //mExitDialog = new CustomDialog(this, mExitView, R.style.MyDialog);
                mExitView = View.inflate(getContext(), R.layout.dialog_exit, null);
                mExitDialog = new CustomDialog(getContext(), mExitView, R.style.MyDialog);
                mExitView.findViewById(R.id.tvExitAccount).setOnClickListener(v1 -> {
                    RongIMClient.getInstance().logout();
                    UserCache.clear();
                    mExitDialog.dismiss();
                    MyApp.exit();
                    ((MainActivity) getActivity()).jumpToActivityAndClearTask(LoginActivity.class);
                });
                mExitView.findViewById(R.id.tvExitApp).setOnClickListener(v1 -> {
                    RongIMClient.getInstance().disconnect();
                    mExitDialog.dismiss();
                    MyApp.exit();
                });
            }
            mExitDialog.show();
        });

        mOivInstruction.setOnClickListener(v ->
                ((MainActivity) Objects.requireNonNull(getActivity())).
                        jumpToActivityAndClearTop(InstructionActivity.class));

        mOivMessage.setOnClickListener(v ->
                ((MainActivity) Objects.requireNonNull(getActivity())).
                        jumpToActivityAndClearTop(MessageActivity.class));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }


    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.CHANGE_INFO_FOR_ME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadUserInfo();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.CHANGE_INFO_FOR_ME);
    }

    @Override
    protected MeFgPresenter createPresenter() {
        return new MeFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_me;
    }

    @Override
    public ImageView getIvHeader() {
        return mIvProfile;
    }

    @Override
    public TextView getTvName() {
        return mTvName;
    }

    @Override
    public TextView getTvAccount() {
        return mTvAccount;
    }

}
