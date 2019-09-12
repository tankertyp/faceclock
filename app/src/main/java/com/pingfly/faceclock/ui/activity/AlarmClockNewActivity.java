package com.pingfly.faceclock.ui.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.lqr.optionitemview.OptionItemView;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.AlarmClock;
import com.pingfly.faceclock.manager.BroadcastManager;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.presenter.AlarmClockNewAtPresenter;
import com.pingfly.faceclock.ui.view.IAlarmClockNewAtView;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;
import com.zhy.autolayout.AutoFrameLayout;

import java.util.Collection;
import java.util.TreeMap;

import butterknife.BindView;



/**
 * 新建闹钟activity
 */
public class AlarmClockNewActivity extends BaseActivity<IAlarmClockNewAtView, AlarmClockNewAtPresenter>
        implements IAlarmClockNewAtView {

    public static final int REQUEST_RING_SELECT = 1;
    private static final String TAG = "AlarmClockNewActivity";

    private AlarmClock mAlarmClock;
    private String mAlarmClockInfo;
    private String countDown;   // 响铃倒计时

    private Boolean isMondayChecked = false;    // 周一按钮状态，默认未选中
    private Boolean isTuesdayChecked = false;   // 周二按钮状态，默认未选中
    private Boolean isWednesdayChecked = false; // 周三按钮状态，默认未选中
    private Boolean isThursdayChecked = false;  // 周四按钮状态，默认未选中
    private Boolean isFridayChecked = false;    // 周五按钮状态，默认未选中
    private Boolean isSaturdayChecked = false;  // 周日按钮状态，默认未选中
    private Boolean isSundayChecked = false;    // 周日按钮状态，默认未选中
    private StringBuilder mRepeatStr;   // 保存重复描述信息String
    private TextView mRepeatDescribe;   //重复描述组件
    private TreeMap<Integer, String> mMap;  //按键值顺序存放重复描述信息

    private TextView mRingDescribe;     //铃声描述
    // final关键字的用法:
    // final修饰的变量为常量，只能赋值一次，赋值后不可修改。必须初始化，初始化必须在声明时或者构造方法中直接赋值。不能通过函数赋值。
    // final方法不能被子类重写
    // final类不能被继承
    final SharedPreferences sharePreferences = getSharedPreferences(AppConst.EXTRA_FACECLOCK_SHARE,AppCompatActivity.MODE_PRIVATE);
    final int volume = sharePreferences.getInt(AppConst.ALARM_VOLUME, 8);  // 音量



    @BindView(R.id.flToolbar)
    AutoFrameLayout toolBar;
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


    @BindView(R.id.tbMonday)
    ToggleButton mTbMonday;
    @BindView(R.id.tbTuesday)
    ToggleButton mTbThuesday;
    @BindView(R.id.tbWednesday)
    ToggleButton mTbWednesday;
    @BindView(R.id.tbThursday)
    ToggleButton mTbThursday;
    @BindView(R.id.tbFriday)
    ToggleButton mTbFriday;
    @BindView(R.id.tbSaturday)
    ToggleButton mTbSaturday;
    @BindView(R.id.tbSunday)
    ToggleButton mTbSunday;

    @BindView(R.id.llRing)
    LinearLayout mLlRing;
    @BindView(R.id.oivRingDescribe)
    OptionItemView mOivRingDescribe;    // 铃声
    @BindView(R.id.sbVolume)
    SeekBar mSbVolume;          // 音量


    @Override
    public void initView() {
        //toolBar.setVisibility(View.GONE);
        mBtnCancel.setText(UIUtils.getString(R.string.cancel));
        mBtnCancel.setText(UIUtils.getString(R.string.save));
        setToolbarTitle(UIUtils.getString(R.string.alarm_clock_new));


        mTpTimePicker.setIs24HourView(true);

        // 新建闹钟时默认铃声
        SharedPreferences share = this.getSharedPreferences(AppConst.EXTRA_FACECLOCK_SHARE, Activity.MODE_PRIVATE);
        String ringName = share.getString(AppConst.RING_NAME, getString(R.string.default_ring));
        String ringUrl = share.getString(AppConst.RING_URL, AppConst.DEFAULT_RING_URL);

        mAlarmClock.setTag(UIUtils.getString(R.string.alarm_clock));    // 初始化闹钟实例的标签
        mAlarmClock.setRingName(ringName);// 初始化闹钟实例的铃声名
        mAlarmClock.setRingUrl(ringUrl);// 初始化闹钟实例的铃声播放地址
        mAlarmClock.setTag(getString(R.string.alarm_clock));// 初始化闹钟实例的标签
        mAlarmClock.setRepeat(getString(R.string.repeat_once)); // 初始化闹钟实例的重复
        mAlarmClock.setWeeks(null);
        mSbVolume.setProgress(volume);  // 设置默认音量显示
    }

    @Override
    public void initData() {

    }

    public void initListener() {

        mEtTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("")) {
                    mAlarmClock.setTag(s.toString());
                } else {
                    mAlarmClock.setTag(getString(R.string.alarm_clock));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /**
         * 监听TimePicker控件的触摸点击事件
         */
        mTpTimePicker.setOnTimeChangedListener((view,hourOfDay,minute)->{

            // 保存闹钟实例的小时
                mAlarmClock.setHour(hourOfDay);
                // 保存闹钟实例的分钟
                mAlarmClock.setMinute(minute);
                // 计算倒计时显示
                mPresenter.displayCountDown();

        });

        //  按键值顺序存放重复描述信息: private TreeMap<Integer, String> mMap;
        mTbMonday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isMondayChecked = true;
                mMap.put(1, getString(R.string.one_h));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isMondayChecked = false;
                mMap.remove(1);
                setRepeatDescribe();
                //displayCountDown();
            }
        });
        mTbThuesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isTuesdayChecked = true;
                mMap.put(2, getString(R.string.two_h));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isTuesdayChecked = false;
                mMap.remove(2);
                setRepeatDescribe();
                // displayCountDown();
            }
        });
        mTbWednesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isWednesdayChecked = true;
                mMap.put(3, getString(R.string.three_h));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isWednesdayChecked = false;
                mMap.remove(3);
                setRepeatDescribe();
                //displayCountDown();
            }
        });
        mTbThursday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isThursdayChecked = true;
                mMap.put(4, getString(R.string.four_h));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isThursdayChecked = false;
                mMap.remove(4);
                setRepeatDescribe();
                //displayCountDown();
            }
        });
        mTbFriday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isFridayChecked = true;
                mMap.put(5, getString(R.string.five_h));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isFridayChecked = false;
                mMap.remove(5);
                setRepeatDescribe();
                //displayCountDown();
            }
        });
        mTbSaturday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isSaturdayChecked = true;
                mMap.put(6, getString(R.string.six_h));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isSaturdayChecked = false;
                mMap.remove(6);
                setRepeatDescribe();
                //displayCountDown();
            }
        });
        mTbSunday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isSundayChecked = true;
                mMap.put(7, getString(R.string.day));
                setRepeatDescribe();
                //displayCountDown();
            } else {
                isSundayChecked = false;
                mMap.remove(7);
                setRepeatDescribe();
                //displayCountDown();
            }
        });


        // 点击铃声选择item转跳进RingSelectActivity,要重写onActivityResult
        mOivRingDescribe.setOnClickListener(v -> {
            // 在两个Activity之间传递数据，最终都是通过Intent传递.
            Intent intent = new Intent(this, RingSelectActivity.class);
            startActivityForResult(intent, REQUEST_RING_SELECT);
        });


        mSbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             *
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 保存设置的音量
                mAlarmClock.setVolume(seekBar.getProgress());
                final SharedPreferences.Editor editor = sharePreferences.edit();
                editor.putInt(AppConst.ALARM_VOLUME, seekBar.getProgress());
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

            }
        });

        // 点击新建“新建闹钟”界面的保存按钮则将新建的闹钟信息通过蓝牙发送给nanopi m4
        mBtnSave.setOnClickListener(v -> mPresenter.sendAlarmClockInfo());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    /**
     * 结束新建闹钟界面时开启渐变缩小效果动画
     */
    private void drawAnimation() {
        finish();
        overridePendingTransition(0, R.anim.zoomout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_RING_SELECT:   // RingSelectActivity的返回数据
                if (data != null) {
                    // 铃声名
                    String name = data.getStringExtra(AppConst.RING_NAME);
                    // 铃声地址
                    String url = data.getStringExtra(AppConst.RING_URL);
                    // 铃声选择界面位置
                    //getIntExtra函数的第二个参数是默认值，也就是不能成功获取数据的时候的函数返回值
                    int ringPager = data.getIntExtra(AppConst.RING_PAGER,0);

                    mRingDescribe.setText(name);
                    mAlarmClock.setRingName(name);
                    mAlarmClock.setRingUrl(url);
                    mAlarmClock.setRingPager(ringPager);
                }
        }

    }

    /**
     * 设置重复描述的内容
     */
    private void setRepeatDescribe() {
        // 全部选中
        if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mRepeatDescribe.setText(getResources().getString(R.string.every_day));
            mAlarmClock.setRepeat(getString(R.string.every_day));
            // 响铃周期
            mAlarmClock.setWeeks("2,3,4,5,6,7,1");
            // 周一到周五全部选中
        } else if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.week_day));
            mAlarmClock.setRepeat(getString(R.string.week_day));
            mAlarmClock.setWeeks("2,3,4,5,6");
            // 周六、日全部选中
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.week_end));
            mAlarmClock.setRepeat(getString(R.string.week_end));
            mAlarmClock.setWeeks("7,1");
            // 没有选中任何一个
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            mRepeatDescribe.setText(getString(R.string.repeat_once));
            mAlarmClock.setRepeat(getResources()
                    .getString(R.string.repeat_once));
            mAlarmClock.setWeeks(null);

        } else {
            mRepeatStr.setLength(0);
            mRepeatStr.append(getString(R.string.week));
            Collection<String> col = mMap.values();
            for (String aCol : col) {
                mRepeatStr.append(aCol).append(getResources().getString(R.string.caesura));
            }
            // 去掉最后一个","
            mRepeatStr.setLength(mRepeatStr.length() - 1);
            mRepeatDescribe.setText(mRepeatStr.toString());
            mAlarmClock.setRepeat(mRepeatStr.toString());

            mRepeatStr.setLength(0);
            if (isMondayChecked) {
                mRepeatStr.append("2,");
            }
            if (isTuesdayChecked) {
                mRepeatStr.append("3,");
            }
            if (isWednesdayChecked) {
                mRepeatStr.append("4,");
            }
            if (isThursdayChecked) {
                mRepeatStr.append("5,");
            }
            if (isFridayChecked) {
                mRepeatStr.append("6,");
            }
            if (isSaturdayChecked) {
                mRepeatStr.append("7,");
            }
            if (isSundayChecked) {
                mRepeatStr.append("1,");
            }
            mAlarmClock.setWeeks(mRepeatStr.toString());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e(TAG, "onResume: " );

    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.NEW_ALARM_CLOCK_FOR_SELECT_RING, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    //RingSelectItem ringSelectItem = intent.getParcelableExtra("");
                    //mPresenter.setRing(ringSelectItem);  // 加载选择好的铃声

            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.NEW_ALARM_CLOCK_FOR_SELECT_RING);
    }

    @Override
    protected AlarmClockNewAtPresenter createPresenter() {
        return new AlarmClockNewAtPresenter(this);
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

    @Override
    public OptionItemView getOivRingDescribe() {
        return mOivRingDescribe;
    }

}



