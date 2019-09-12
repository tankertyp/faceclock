package com.pingfly.faceclock.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.ui.adapter.CommonFragmentPagerAdapter;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BaseFragment;
import com.pingfly.faceclock.ui.fragment.FragmentFactory;
import com.pingfly.faceclock.ui.presenter.MainAtPresenter;
import com.pingfly.faceclock.ui.view.IMainAtView;
import com.pingfly.faceclock.util.UIUtils;
import com.zhy.autolayout.AutoFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *   MainActivity主要是界面组件的绑定，以及一些成员变量的初始化
 */
public class MainActivity extends BaseActivity<IMainAtView, MainAtPresenter>
        implements ViewPager.OnPageChangeListener, IMainAtView{

    private static final String TAG = "MainActivity";
    private List<BaseFragment> mFragmentList = new ArrayList<>(3);
    private long firstTime = 0L;

    @BindView(R.id.flToolbar)
    AutoFrameLayout toolBar;

    @BindView(R.id.vpContent)
    ViewPager mVpContent;
    //底部
    @BindView(R.id.llHome)
    LinearLayout mLlHome;
    @BindView(R.id.tvHomeNormal)
    TextView mTvHomeNormal;
    @BindView(R.id.tvHomePress)
    TextView mTvHomePress;
    @BindView(R.id.tvHomeTextNormal)
    TextView mTvHomeTextNormal;
    @BindView(R.id.tvHomeTextPress)
    TextView mTvHomeTextPress;

    @BindView(R.id.llDiscovery)
    LinearLayout mLlDiscovery;
    @BindView(R.id.tvDiscoveryNormal)
    TextView mTvDiscoveryNormal;
    @BindView(R.id.tvDiscoveryPress)
    TextView mTvDiscoveryPress;
    @BindView(R.id.tvDiscoveryTextNormal)
    TextView mTvDiscoveryTextNormal;
    @BindView(R.id.tvDiscoveryTextPress)
    TextView mTvDiscoveryTextPress;

    @BindView(R.id.llMe)
    LinearLayout mLlMe;
    @BindView(R.id.tvMeNormal)
    TextView mTvMeNormal;
    @BindView(R.id.tvMePress)
    TextView mTvMePress;
    @BindView(R.id.tvMeTextNormal)
    TextView mTvMeTextNormal;
    @BindView(R.id.tvMeTextPress)
    TextView mTvMeTextPress;


    @Override
    public void init() {
        registerBR();
    }

    @Override
    public void initView() {
        toolBar.setVisibility(View.GONE);
        //setToolbarTitle(UIUtils.getString(R.string.app_name));
        //mIbAddMenu.setVisibility(View.VISIBLE);

        //等待全局数据获取完毕
        //showWaitingDialog(UIUtils.getString(R.string.please_wait));

        //默认选中第一个
        setTransparency();
        mTvHomePress.getBackground().setAlpha(255);
        mTvHomeTextPress.setTextColor(Color.argb(255, 69, 192, 26));
        //mTvHomeTextPress.setTextColor(getResources().getColor(R.color.main_blue));
        //设置ViewPager的最大缓存页面
        mVpContent.setOffscreenPageLimit(2);

        mFragmentList.add(FragmentFactory.getInstance().getHomeFragment());
        mFragmentList.add(FragmentFactory.getInstance().getDiscoveryFragment());
        mFragmentList.add(FragmentFactory.getInstance().getMeFragment());
        mVpContent.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList));
    }


    @Override
    public void initListener() {

        mLlHome.setOnClickListener(v -> bottomBtnClick(v));
        mLlDiscovery.setOnClickListener(v -> bottomBtnClick(v));
        mLlMe.setOnClickListener(v -> bottomBtnClick(v));
        mVpContent.setOnPageChangeListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    // 获得状态栏的高度
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public void bottomBtnClick(View view) {
        setTransparency();
        switch (view.getId()) {
            case R.id.llHome:
                mVpContent.setCurrentItem(0, false);
                mTvHomePress.getBackground().setAlpha(255);
                //mTvHomeTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                mTvHomeTextPress.setTextColor(getResources().getColor(R.color.main_blue));
                break;
            case R.id.llDiscovery:
                mVpContent.setCurrentItem(1, false);
                mTvDiscoveryPress.getBackground().setAlpha(255);
                //mTvDiscoveryTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                mTvDiscoveryTextPress.setTextColor(getResources().getColor(R.color.main_blue));
                break;
            case R.id.llMe:
                mVpContent.setCurrentItem(2, false);
                mTvMePress.getBackground().setAlpha(255);
                //mTvMeTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                mTvMeTextPress.setTextColor(getResources().getColor(R.color.main_blue));
                break;
        }
    }

    /**
     * 把press图片、文字全部隐藏(设置透明度)
     */
    private void setTransparency() {
        mTvHomeNormal.getBackground().setAlpha(255);
        mTvDiscoveryNormal.getBackground().setAlpha(255);
        mTvMeNormal.getBackground().setAlpha(255);
        mTvHomePress.getBackground().setAlpha(1);
        mTvDiscoveryPress.getBackground().setAlpha(1);
        mTvMePress.getBackground().setAlpha(1);
        mTvHomeTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvDiscoveryTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvMeTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvHomeTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        //mTvHomeTextPress.setTextColor(getResources().getColor(R.color.main_blue));
        mTvDiscoveryTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        mTvMeTextPress.setTextColor(Color.argb(0, 69, 192, 26));
    }

    @Override
    protected MainAtPresenter createPresenter() {
        return new MainAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isToolbarCanBack() {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //根据ViewPager滑动位置更改透明度
        int diaphaneity_one = (int) (255 * positionOffset);
        int diaphaneity_two = (int) (255 * (1 - positionOffset));
        switch (position) {

            case 0:
                mTvHomeNormal.getBackground().setAlpha(diaphaneity_one);
                mTvHomePress.getBackground().setAlpha(diaphaneity_two);
                mTvDiscoveryNormal.getBackground().setAlpha(diaphaneity_two);
                mTvDiscoveryPress.getBackground().setAlpha(diaphaneity_one);
                mTvHomeTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                //mTvHomeTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                mTvHomeTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 26, 192));
                mTvDiscoveryTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                //mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 192, 26));
                mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 26, 192));
                break;

            case 1:
                mTvDiscoveryNormal.getBackground().setAlpha(diaphaneity_one);
                mTvDiscoveryPress.getBackground().setAlpha(diaphaneity_two);
                mTvMeNormal.getBackground().setAlpha(diaphaneity_two);
                mTvMePress.getBackground().setAlpha(diaphaneity_one);
                mTvDiscoveryTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                //mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 26, 192));
                mTvMeTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                //mTvMeTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 192, 26));
                mTvMeTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 26, 192));
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.FETCH_COMPLETE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideWaitingDialog();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.FETCH_COMPLETE);
    }

    /**
     *程序优化，点击两次退出程序
     * */
    //@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 800L) {
                //Toast.makeText(this, R.string.back_to_setting_view, Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;

            }
        }
        return true;
    }

}
