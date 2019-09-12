package com.pingfly.faceclock.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gxz.PagerSlidingTabStrip;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.RingSelectItem;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.fragment.LocalMusicFragment;
import com.pingfly.faceclock.ui.fragment.NanopiMusicFragment;
import com.pingfly.faceclock.util.UIUtils;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;

/**
 * 铃声选择fragment
 *
 */

public class RingSelectActivity extends BaseActivity  {

    @BindView(R.id.btnToolbarSend)
    Button mBtnToolbarSend;

    @BindView(R.id.ring_select_llyt)
    ViewGroup viewGroup;    // 铃声选择界面

    private List<Fragment> mFragmentList;   // 铃声种类集合
    public static String sRingName; // 铃声名
    public static String sRingUrl; // 铃声地址
    public static int sRingPager;   // 铃声界面
    public static int sRingRequestType; //铃声请求类型



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取得新建闹钟Activity传递过来的铃声信息参数
        Intent intent = getIntent();
        sRingName = intent.getStringExtra(AppConst.RING_NAME);
        sRingUrl = intent.getStringExtra(AppConst.RING_URL);
        sRingPager = intent.getIntExtra(AppConst.RING_PAGER, -1);
        sRingRequestType = intent.getIntExtra(AppConst.RING_REQUEST_TYPE, 0);
    }


    @Override
    public void initView() {
        mBtnToolbarSend.setText(UIUtils.getString(R.string.save));
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        setToolbarTitle(UIUtils.getString(R.string.ring_select));
        initFragment(); // 设置显示铃声列表的Fragment
    }


    public void initListener() {

        mBtnToolbarSend.setOnClickListener(v ->{
            // 取得选中的铃声信息
            String ringName = RingSelectItem.getInstance().getName();
            String ringUrl = RingSelectItem.getInstance().getUrl();
            int ringPager = RingSelectItem.getInstance().getRingPager();

            // 保存选中的铃声信息
            SharedPreferences share = getSharedPreferences(
                    AppConst.EXTRA_FACECLOCK_SHARE, Activity.MODE_PRIVATE);
            SharedPreferences.Editor edit = share.edit();

            // 来自闹钟请求
            if (sRingRequestType == 0) {
                edit.putString(AppConst.RING_NAME, ringName);
                edit.putString(AppConst.RING_URL, ringUrl);
                edit.putInt(AppConst.RING_PAGER, ringPager);
                // 计时器请求
            }

            edit.apply();

            // 传递选中的铃声信息
            Intent i = new Intent();
            i.putExtra(AppConst.RING_NAME, ringName);
            i.putExtra(AppConst.RING_URL, ringUrl);
            i.putExtra(AppConst.RING_PAGER, ringPager);
            setResult(Activity.RESULT_OK, i);
            finish();
        });
    }

    /**
     * 设置显示铃声列表的Fragment
     */
    private void initFragment() {

        // 展示Nanopi M4铃声的Fragment
        NanopiMusicFragment nanopiMusicFragment = new NanopiMusicFragment();
        // 展示录音的Fragment
        //RecorderFragment recordFragment = new RecorderFragment();
        // 展示系统铃声的Fragment

        // 展示本地铃声的Fragment
        LocalMusicFragment localMusicFragment = new LocalMusicFragment();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(nanopiMusicFragment);
        mFragmentList.add(localMusicFragment);

    }

    /**
     * 设置ViewPager并与开源组件【PagerSlidingTabStrip】相关联
     *
     * @param view view
     */
    private void initViewPager(View view) {
//      铃声种类
        ViewPager viewPager = (ViewPager) view
                .findViewById(R.id.fragment_ring_select_sort);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // 铃声界面位置
        int currentIndex;
        // 当由编辑闹钟界面跳转时
        if (sRingPager != -1) {
            // 设置闹钟界面为保存的铃声界面位置
            viewPager.setCurrentItem(sRingPager);
            currentIndex = sRingPager;
        } else {
            // 取得最近一次选择的闹钟界面位置信息
            SharedPreferences shares = getSharedPreferences(
                    AppConst.EXTRA_FACECLOCK_SHARE, AppCompatActivity.MODE_PRIVATE);
            int position = shares.getInt(AppConst.RING_PAGER, 0);
            viewPager.setCurrentItem(position);
            currentIndex = position;
        }
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabstrip);
        tabs.setViewPager(viewPager);
        // 设置当前铃声界面位置，来初始化选中文字颜色
        //tabs.setCurrentIndex(currentIndex);
        tabs.setTypeface(Typeface.DEFAULT, 0);  // 普通字体
    }

    /**
     * 铃声种类ViewPager适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        /**
         * 铃声选择列表标题
         */
        private final String[] titles = {getString(R.string.nanopi_music),
                getString(R.string.local_music)};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        // 开源控件PagerSlidingTabStrip需要通过它获取标签标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_ring_select;
    }

}