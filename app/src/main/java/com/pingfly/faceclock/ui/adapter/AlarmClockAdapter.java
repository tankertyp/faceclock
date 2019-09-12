package com.pingfly.faceclock.ui.adapter;

import android.content.Context;
//import android.support.v7.widget.RecyclerView;
import android.content.Intent;


import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;


import com.bumptech.glide.request.RequestOptions;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.AlarmClock;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.ui.activity.AlarmClockModeActivity;
import com.pingfly.faceclock.ui.activity.AlarmClockNewActivity;
import com.pingfly.faceclock.ui.presenter.AlarmClockModeAtPresenter;
import com.pingfly.faceclock.util.MyUtils;
import com.pingfly.faceclock.util.RxBus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.rong.imlib.model.UserInfo;

/**
 * 保存闹钟信息的adapter
 */
public abstract class AlarmClockAdapter extends MyAdapterForRecyclerView<AlarmClock> {

    public UserInfo mUserInfo;

    private Context mContext;
    private List<AlarmClock> mData;

    private AlarmClockModeAtPresenter mPresenter;
    private int mWhite; // 白色
    private int mWhiteTrans;    // 淡灰色
    private boolean inDeletionMode = false; //是否删除模式
    private boolean isCanClick = true;

    /*
        适配器格式:
        public MyAdapter(Context context, List<DataBean> data) {
            mContext = context;
            mData = data;
        }
     */

    public AlarmClockAdapter(Context context, List<AlarmClock> data, AlarmClockModeAtPresenter presenter) {
        super(context, data);
        mContext = context;
        mData = data;
        mPresenter = presenter;
    }

    /**
     * 在convert方法中对item进行数据设置
     */
    @Override
    public void convert(MyViewHolderForRecyclerView helper, AlarmClock alarmClock, int position) {   // 在convert方法中对item进行数据设置

        setView(helper, alarmClock, position);
        setOnClick(helper, alarmClock, position);

    }


    /**
     * 显示闹钟信息
     *
     * @param helper
     * @param alarmClock
     * @param position
     */
    private void setView(MyViewHolderForRecyclerView helper, AlarmClock alarmClock, int position) {

        // helper的使用:
        // MyViewHolderForAbsListView中提供了许多常规用的控件操作，如设置文字、文字颜色、背景、显隐等，
        // 同时每个方法都是返回this，这意味着可以链式操作，方便快速开发。
        UserInfo userInfo = DBManager.getInstance().getUserInfo(alarmClock.getUserId());

        if (userInfo != null) {
            ImageView ivHeader = helper.getView(R.id.ivHeader);
            Glide.with(mContext).load(userInfo.getPortraitUri()).apply(RequestOptions.centerCropTransform()).into(ivHeader);
            helper.setText(R.id.tvName, userInfo.getName())
                    .setText(R.id.tvRingName, alarmClock.getRingName())
                    .setText(R.id.tvTime, MyUtils.formatTime(alarmClock.getHour(), alarmClock.getMinute()))
                    .setViewVisibility(R.id.tvRingName, View.VISIBLE)
                    .setViewVisibility(R.id.tbAlarmClockOnOff, View.VISIBLE);
                    //.setViewVisibility(R.id.cbDelete, View.GONE);
        }

        // 设置闹钟模式的背景以及闹钟item的背景
        //helper.setBackgroundColor(R.id.flRoot, alarmClock.isOnOff() ? R.color.gray8 : android.R.color.white);

    }

    private void setOnClick(MyViewHolderForRecyclerView helper, AlarmClock alarmClock, int position) {


        ImageView mIvBack = helper.getView(R.id.ivToolbarNavigation);

        mIvBack.setVisibility(inDeletionMode ? View.GONE : View.VISIBLE);
        // 非删除模式
        helper.getView(R.id.fabAlarmClockNew).setOnClickListener(v -> {
            //AlarmClock alarmClock = DBManager.getInstance().getAlarmClockInfo(item.getSenderUserId());
            if (alarmClock != null) {
                Intent intent = new Intent(mContext, AlarmClockNewActivity.class);
                intent.putExtra("alarmClock", alarmClock);
                ((AlarmClockModeActivity) mContext).jumpToActivity(intent);
            }
        });


    }

    //设置是否为删除模式，改变绑定视图
    public void setInDeletionMode(boolean inDeletionMode) {
        this.inDeletionMode = inDeletionMode;
        notifyDataSetChanged();
    }

    // 选中要删除用户
    private Set<AlarmClock> selectSet = new HashSet<>();

    // 获取选中要删除的列表
    public Set<AlarmClock> getSelectSet() {
        return selectSet;
    }

    public void setSelectSet(Set<AlarmClock> selectSet) {
        this.selectSet = selectSet;
    }

}