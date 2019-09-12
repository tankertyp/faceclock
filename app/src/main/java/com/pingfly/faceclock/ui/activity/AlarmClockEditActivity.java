package com.pingfly.faceclock.ui.activity;


import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lqr.optionitemview.OptionItemView;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.presenter.AlarmClockEditAtPresenter;
import com.pingfly.faceclock.ui.presenter.AlarmClockNewAtPresenter;
import com.pingfly.faceclock.ui.view.IAlarmClockEditAtView;
import com.pingfly.faceclock.ui.view.IAlarmClockNewAtView;
import com.pingfly.faceclock.util.UIUtils;

import butterknife.BindView;

/*
 * 闹钟修改activity
 */
public class AlarmClockEditActivity extends BaseActivity<IAlarmClockEditAtView, AlarmClockEditAtPresenter>
        implements IAlarmClockEditAtView {


    @BindView(R.id.btnCancel)
    Button mBtnCancel;          // 取消
    @BindView(R.id.btnSave)
    Button mBtnSave;            // 保存
    @BindView(R.id.tvTimePicker)
    TextView mTvTimePicker;
    @BindView(R.id.tpTimePicker)// 闹钟时间选择器
    TimePicker mTpTimePicker;
    @BindView(R.id.etTag)
    EditText mEtTag;
    @BindView(R.id.tvRepeatDescribe)
    TextView mTvRepeatDescribe;
    @BindView(R.id.sbVolume)
    SeekBar mSbVolume;          // 音量


    public void initView(){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 按下返回键开启移动退出动画
        overridePendingTransition(0, R.anim.move_out_bottom);
    }


    @Override
    protected AlarmClockEditAtPresenter createPresenter() {
        return new AlarmClockEditAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_alarm_clock_new_edit;
    }

    @Override
    public TextView getTvTimePicker() {
        return mTvTimePicker;
    }

    @Override
    public TextView getTvRepeatDescribe() {
        return mTvRepeatDescribe;
    }

    @Override
    public SeekBar getSbVolume() {
        return mSbVolume;
    }

}
