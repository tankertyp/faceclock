package com.pingfly.faceclock.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.AlarmClock;
import com.pingfly.faceclock.db.DBManager;
import com.pingfly.faceclock.model.cache.UserCache;
import com.pingfly.faceclock.ui.activity.AlarmClockEditActivity;
import com.pingfly.faceclock.ui.activity.AlarmClockModeActivity;
import com.pingfly.faceclock.ui.adapter.AlarmClockAdapter;
import com.pingfly.faceclock.ui.adapter.MyAdapterForRecyclerView;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IAlarmClockModeAtView;
import com.pingfly.faceclock.util.MyUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmClockModeAtPresenter extends BasePresenter<IAlarmClockModeAtView> {

    private static final int REQUEST_ALARM_CLOCK_NEW = 1;   // 新建闹钟的requestCode
    private static final int REQUEST_ALARM_CLOCK_EDIT = 2;  // 修改闹钟的requestCode

    // 这里的mData就相当于mAlarmClockList
    private List<AlarmClock> mData = new ArrayList<>();   // 保存闹钟信息的list
    private List<AlarmClock> mSelectedData = new ArrayList<>();

    private AlarmClockAdapter mAdapter;     // 保存闹钟信息的adapter
    private MyAdapterForRecyclerView<AlarmClock> mSelectedAdapter;
    private PopupWindow popupWindowDelete;  // 底部删除弹窗


    /**
     * List内容为空时的视图
     */
    private LinearLayout mEmptyView;


    public AlarmClockModeAtPresenter(BaseActivity context) {
        super(context);
    }


    /**
     * 加载闹钟列表
     */
    public void loadAlarmClocks() {

        loadData();
        setAdapter();
    }

    /**
     * 加载闹钟数据
     */
    private void loadData() {

        List<AlarmClock> alarmClock = DBManager.getInstance().getAlarmClocks();
        if (alarmClock != null && alarmClock.size() > 0) {
            mData.clear();
            mData.addAll(alarmClock);

            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
    }

    private void setAdapter() {
        if (mAdapter == null) { // 创建适配器
            // LQRAdapterForAbsListView<数据类型>（上下文，数据集合，item的布局引用）
            mAdapter = new AlarmClockAdapter(mContext, mData, this) {
                // 如下是监听闹铃item点击事件Listener
                //OnItemClickListener onItemClickListener = new OnItemClickListenerImpl();
                // 不管是LQRAdapterForAbsListView还是LQRAdapterForRecyclerView，都可以通过使用适配器对item进行事件监听

            };

            // 点击闹钟item, 进入闹钟编辑AlarmClockEditActivity
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                AlarmClock alarmClock = mData.get(position);
                Intent intent = new Intent(mContext, AlarmClockEditActivity.class);
                intent.putExtra(AppConst.ALARM_CLOCK, alarmClock);
                // 开启编辑闹钟界面
                mContext.startActivityForResult(intent, AlarmClockModeActivity.REQUEST_ALARM_CLOCK_EDIT);
                // 启动移动进入效果动画
                mContext.overridePendingTransition(R.anim.move_in_bottom, 0);
            });

            //长按item，隐藏floatingbtn，显示全选，底部删除弹出
            mAdapter.setOnItemLongClickListener((helper, parent, itemView, position) -> {
                View popupView = View.inflate(mContext, R.layout.popup_window_delete, null);
                popupWindowDelete = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);   // 参数2,3：指明popupwindow的宽度和高度
                AlarmClock item = mData.get(position);
                mAdapter.notifyDataSetChanged();

                if (!popupWindowDelete.isShowing()) {
                    backgroundAlpha(0.6f);
                    popupWindowDelete.setBackgroundDrawable(new BitmapDrawable());
                    //点击外部消失，这里因为PopupWindow填充了整个窗口，所以这句代码就没用了
                    popupWindowDelete.setOutsideTouchable(true);
                    //设置可以点击
                    popupWindowDelete.setTouchable(true);
                    //进入退出的动画
                    popupWindowDelete.setAnimationStyle(R.style.popwin_anim_style);

                    // 标题栏的显隐
                    //mContext.setToolbarTitle(UIUtils.getString(R.string.select_alarm_clock));
                    getView().getTvSelectAll().setVisibility(View.VISIBLE);
                    getView().getTvSelectAllNot().setVisibility(View.GONE);
                    getView().getFabAlarmClockNew().setVisibility(View.GONE);


                    //通知adapter变为删除选择模式，将当前选中的item的checkbox设置为true
                    mAdapter.setOnItemClickListener((MyViewHolder, viewGroup, view, i) -> {

                        // 选中或反选
                        AlarmClock alarmClock = mData.get(i - 1);
                        if (mSelectedData.contains(alarmClock)) {
                            mSelectedData.remove(alarmClock);
                        } else {
                            mSelectedData.add(alarmClock);
                        }
                        mSelectedAdapter.notifyDataSetChangedWrapper();
                        //mAdapter.notifyDataSetChanged();
                        if(mSelectedData.size() > 0) {
                            popupWindowDelete.setTouchable(true) ;
                        }else {
                            popupWindowDelete.setTouchable(false);
                        }

                        popupWindowDelete.setTouchInterceptor(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getY()>=0){//PopupWindow内部的事件
                                    return false;
                                }

                                if (mSelectedData == null) {
                                  deleteAlarmClocks();
                                } else {
                                    //添加群成员
                                    addAlarmClocks();
                                }

                                if (popupWindowDelete.isShowing()) {
                                    backgroundAlpha(1.0f);
                                    popupWindowDelete.setAnimationStyle(R.anim.popup_anim_out);  //设置加载动画
                                }
                                return true;
                            }
                        });
                    });

                }
                return false;
            });
            getView().getRvAlarmClock().setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
            if (getView() != null && getView().getRvAlarmClock() != null)
                rvMoveToTop();
        }

    }




    public void popupWindowDelete(){
        DBManager.getInstance().deleteAlarmClocks();
        mAdapter.notifyDataSetChangedWrapper();
        popupWindowDelete.dismiss();   // 在点击之后设置popupwindow的销毁
        backgroundAlpha(1.0f);
    }

    private void rvMoveToTop() {
        getView().getRvAlarmClock().smoothMoveToPosition(0);
    }

    /**
     * 设置屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }




    //点击全选，全不选，状态切换
    private void seletAllOrNot() {
        if (mAdapter.getSelectSet().size() < mData.size()) {
            Set<AlarmClock> selectSet = mAdapter.getSelectSet();
            selectSet.addAll(mData);
            mAdapter.setSelectSet(selectSet);
            mAdapter.notifyDataSetChangedWrapper();
            //setAheadText("全不选");
            getView().getTvSelectAll().setVisibility(View.GONE);
            getView().getTvSelectAllNot().setVisibility(View.VISIBLE);
        } else {
            mAdapter.setSelectSet(new HashSet<AlarmClock>());
            mAdapter.notifyDataSetChangedWrapper();
            //setAheadText("全选");
            getView().getTvSelectAll().setVisibility(View.VISIBLE);
            getView().getTvSelectAllNot().setVisibility(View.GONE);
        }
    }

    //activity监听到adapter选择了全部的item，全选变成全不选
    public void selectedAll() {

        mContext.setToolbarTitle("已选择" + mAdapter.getSelectSet().size() + "项");
        getView().getTvSelectAll().setVisibility(View.GONE);
        getView().getTvSelectAllNot().setVisibility(View.VISIBLE);
    }


    private boolean isShow() {
        SharedPreferences share = mContext.getSharedPreferences(
                AppConst.EXTRA_FACECLOCK_SHARE, AppCompatActivity.MODE_PRIVATE);
        return share.getBoolean(AppConst.ALARM_CLOCK_EXPLAIN, true);
    }


    public void addAlarmClocks() {
        ArrayList<String> selectedIds = new ArrayList<>(mSelectedData.size());
        for (int i = 0; i < mSelectedData.size(); i++) {
            AlarmClock alarmClock = mSelectedData.get(i);
            selectedIds.add(alarmClock.getUserId());
        }
        Intent data = new Intent();
        data.putStringArrayListExtra("selectedIds", selectedIds);
        mContext.setResult(AppCompatActivity.RESULT_OK, data);
        mContext.finish();
    }

    //删除选中对象
    public void deleteAlarmClocks() {
        mSelectedData.add(0, DBManager.getInstance().getAlarmClockByUserId(UserCache.getId()));
        int size = mSelectedData.size();
        if (size == 0)
            return;
        DBManager.getInstance().deleteAlarmClocks(mSelectedData);
    }

    // 更新闹钟列表
    private void updateAlarmClock() {
        mData.clear();

        List<AlarmClock> list = DBManager.getInstance().loadAlarmClocks();
        for (AlarmClock AlarmClock : list) {
            mData.add(AlarmClock);

            // 当闹钟为开时刷新开启闹钟
            if (AlarmClock.isOnOff()) {
                MyUtils.startAlarmClock(mContext, AlarmClock);
            }
        }
        mAdapter.notifyDataSetChangedWrapper();
    }


    public void addList(AlarmClock ac) {
       mData.clear();

        int id = ac.getId();
        int count = 0;
        int position = 0;
        List<AlarmClock> list = DBManager.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mData.add(alarmClock);

            if (id == alarmClock.getId()) {
                position = count;
                if (alarmClock.isOnOff()) {
                    MyUtils.startAlarmClock(mContext, alarmClock);
                }
            }
            count++;
        }

        checkIsEmpty(list);

        mAdapter.notifyItemInserted(position);
        getView().getRvAlarmClock().scrollToPosition(position);
    }

    private void updateList() {
        mData.clear();

        List<AlarmClock> list = DBManager.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mData.add(alarmClock);

            // 当闹钟为开时刷新开启闹钟
            if (alarmClock.isOnOff()) {
                MyUtils.startAlarmClock(mContext, alarmClock);
            }
        }

        checkIsEmpty(list);

        mAdapter.notifyDataSetChanged();
    }

    private void checkIsEmpty(List<AlarmClock> list) {
        if (list.size() != 0) {
            getView().getTvEmpty().setVisibility(View.GONE);
        } else {
            getView().getTvEmpty().setVisibility(View.VISIBLE);
        }
    }

}


