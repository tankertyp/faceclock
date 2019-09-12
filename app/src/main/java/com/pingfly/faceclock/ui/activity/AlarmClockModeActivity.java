package com.pingfly.faceclock.ui.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqr.recyclerview.LQRRecyclerView;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.AlarmClock;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.ui.adapter.AlarmClockAdapter;

import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BaseFragmentActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.presenter.AlarmClockModeAtPresenter;
import com.pingfly.faceclock.ui.view.IAlarmClockModeAtView;
import com.pingfly.faceclock.util.UIUtils;
import com.zhy.autolayout.AutoFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AlarmClockModeActivity  extends BaseActivity<IAlarmClockModeAtView, AlarmClockModeAtPresenter>
        implements IAlarmClockModeAtView {


    public static final int REQUEST_ALARM_CLOCK_NEW = 1000;   // 新建闹钟的requestCode
    public static final int REQUEST_ALARM_CLOCK_EDIT = 1001;  // 修改闹钟的requestCode

    @BindView(R.id.flToolbar)
    AutoFrameLayout toolBar;
    @BindView(R.id.fabAlarmClockNew)
    FloatingActionButton mFabAlarmClockNew;// 用FloatingActionButton 作为添加按钮。
    @BindView(R.id.rvAlarmClockList)
    LQRRecyclerView mRvAlarmClock;
    @BindView(R.id.tvEmpty)
    TextView mTvEmpty;

    @BindView(R.id.tvSelectAll)
    TextView mTvSelectAll;
    @BindView(R.id.tvSelectAllNot)
    TextView mTvSelectAllNot;
    // PopupWindow并没有继承View,所以下列写法错误
    //@BindView(R.id.popupWindowDelete)
    //@SuppressLint("InflateParams")
    //View contentView = LayoutInflater.from(AlarmClockModeActivity.this).inflate(R.layout.popup_window_delete, null);
    //PopupWindow mPopupWindowDelete = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);;


    /**
     * List内容为空时的视图
     */
    private LinearLayout mEmptyView;
    private List<AlarmClock> mAlarmClockList;   ///保存闹钟信息的list
    public ArrayList<String> mSelectedAlarmClockAccounts;    // 删除闹钟的ArrayList



    /**
     * 保存闹钟信息的adapter
     */
    private AlarmClockAdapter mAdapter;


    /**
     * 初始化View
     */
    @Override
    public void initView() {
        //toolBar.setVisibility(View.GONE);
        setToolbarTitle(UIUtils.getString(R.string.alarm_clock_mode));
        mTvSelectAll.setVisibility(View.GONE);
        mTvSelectAllNot.setVisibility(View.GONE);
    }

    @Override
    public void init() {
        registerBR();
        mSelectedAlarmClockAccounts = getIntent().getStringArrayListExtra("selectedAlarmClock");
    }

    @Override
    public void initData() {

    }

    public void initListener() {

        // 监听点击新建闹钟悬浮按钮
        mFabAlarmClockNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmClockNewActivity.class);
            startActivityForResult(intent,REQUEST_ALARM_CLOCK_NEW );
            this.overridePendingTransition(R.anim.zoomin, 0);
        });

        /**
        mPopupWindowDelete.setTouchInterceptor(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getY()>=0){//PopupWindow内部的事件
                    return false;
                }

                if (mSelectedAlarmClockAccounts == null) {
                    mPresenter.deleteAlarmClocks();
                } else {
                    //添加群成员
                    mPresenter.addAlarmClocks();
                }

                if (mPopupWindowDelete.isShowing()) {
                    mPresenter.backgroundAlpha(1.0f);
                    mPopupWindowDelete.setAnimationStyle(R.anim.popup_anim_out);  //设置加载动画
                }
                return true;
            }
        });
         */

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterBR();
    }

    /**
     * 在一个主界面(主Activity)上能连接往许多不同子功能模块(子Activity上去)，当子模块的事情做完之后就回到主界面，
     * 或许还同时返回一些子模块完成的数据交给主Activity处理。这样的数据交流就要用到回调函数onActivityResult
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        AlarmClock ac = data
                .getParcelableExtra(AppConst.ALARM_CLOCK);
        switch (requestCode) {
            // 新建闹钟
            case REQUEST_ALARM_CLOCK_NEW:
                // 插入新闹钟数据
//                TabAlarmClockOperate.getInstance(getActivity()).insert(ac);
                DBManager.getInstance().saveAlarmClock(ac);
                mPresenter.addList(ac);
                break;
        }
    }



    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.ALARM_CLOCK_LIST_UPDATE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadAlarmClocks();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.ALARM_CLOCK_LIST_UPDATE);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        // 底部删除弹窗显示时按返回键，则设置popupwindow的销毁
        //if ( mPopupWindowDelete.isShowing()) {
        //    mPopupWindowDelete.dismiss();
        //    mPresenter.backgroundAlpha(1.0f);
        //    mFabAlarmClockNew.setVisibility(View.VISIBLE);
        //} else {    // 否则直接退出AlarmClockModeActivity
            super.onBackPressed();
        //}
    }

    @Override
    protected AlarmClockModeAtPresenter createPresenter() {
        return new AlarmClockModeAtPresenter(this);
    }


    @Override
    protected int provideContentViewId() {
        return R.layout.activity_alarm_clock_mode;
    }



    @Override
    public TextView getTvSelectAll() { return mTvSelectAll;}

    @Override
    public TextView getTvSelectAllNot() { return mTvSelectAllNot;}

    @Override
    public ImageView getFabAlarmClockNew() {
        return mFabAlarmClockNew;
    }

    @Override
    public TextView getTvEmpty() {
        return mTvEmpty;
    }

    @Override
    public LQRRecyclerView getRvAlarmClock() {
        return mRvAlarmClock;
    }

    //@Override
    //public PopupWindow getPopupWindowDelete() {
    //    return mPopupWindowDelete;
    //}
}
