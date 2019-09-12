package com.pingfly.faceclock.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.AlarmBaseActivity;
import com.pingfly.faceclock.alarmclock.dao.AlarmInfoDao;
import com.pingfly.faceclock.alarmclock.domain.AlarmClock;
import com.pingfly.faceclock.alarmclock.domain.AlarmInfo;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.alarmclock.view.AddItemView;
import com.pingfly.faceclock.util.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddAlarmActivity extends AlarmBaseActivity implements View.OnClickListener {


    @BindView(R.id.tvToolbarTitle)
    TextView mToolbarTitle;
    @BindView(R.id.ivToolbarNavigation)
    public ImageView mToolbarNavigation;


    //@BindView(R.id.ivCancel)
    //ImageView mIvBack;
    //@BindView(R.id.ivDone)
    //TextView mIvDone;

    private TimePicker mTimePicker;//闹钟定时
    private ImageView mCancel;//后退
    private ImageView mDone;//完成
    private int mHours=6;
    private int mMinute=30;
    private AddItemView mTag;
    private AddItemView mLazyLevel;
    private AddItemView mRing;
    private CheckBox mDay1;
    private CheckBox mDay2;
    private CheckBox mDay3;
    private CheckBox mDay4;
    private CheckBox mDay5;
    private CheckBox mDay6;
    private CheckBox mDay7;
    private int mLevel;
    private String mTagDesc;
    private Intent mIntent;
    private String RingName;
    private String Ringid;
    private List<CheckBox> repeatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        ButterKnife.bind(this);
        //ActionBar ab = getSupportActionBar();
        // 设置返回开启
        //ab.setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        initListener();
    }

    private void initView() {

        setToolbarTitle(UIUtils.getString(R.string.alarm_clock_new));

        mTimePicker = (TimePicker) findViewById(R.id.tp_set_alarm_add);
        mTimePicker.setIs24HourView(true);
        //mBack = (ImageView) findViewById(R.id.iv_back_add);
        //mDone = (TextView) findViewById(R.id.tv_done_add);
        mTag = (AddItemView) findViewById(R.id.aiv_tag_add);
        mLazyLevel = (AddItemView) findViewById(R.id.aiv_lazy_add);
        if(mLazyLevel == null){
            LogUtils.d("赖床指数为null");
        }
        mRing = (AddItemView) findViewById(R.id.aiv_ring_add);
        mLevel=0;
        mTagDesc="闹钟";
        Ringid="everybody.mp3";
        RingName="everybody";
        initCheckBox();
        initBottom();
        mIntent = getIntent();
        if(mIntent.getBooleanExtra("update",false)){
            updateView();
        }
    }

    private void updateView() {
        //
        AlarmInfoDao dao=new AlarmInfoDao(this);
        AlarmInfo alarmInfo=dao.findById(mIntent.getStringExtra("oldId"));
        mTimePicker.setCurrentHour(alarmInfo.getHour());
        mTimePicker.setCurrentMinute(alarmInfo.getMinute());
        //设置checkbox
        for(int i=0;i<alarmInfo.getDayOfWeek().length;i++){
            if(alarmInfo.getDayOfWeek()[0]==0){
                break;
            }else{
                repeatList.get(alarmInfo.getDayOfWeek()[i]-1).setChecked(true);
            }
        }
        mTag.setDesc(alarmInfo.getTag());
        mLazyLevel.setDesc("赖床指数:"+alarmInfo.getLazyLevel()+"级");
        mRing.setDesc(alarmInfo.getRing());

        mHours=alarmInfo.getHour();
        mMinute=alarmInfo.getMinute();
        mLevel=alarmInfo.getLazyLevel();
        mTagDesc=alarmInfo.getTag();
        Ringid=alarmInfo.getRingResId();
        RingName=alarmInfo.getRing();
    }

    private void initCheckBox() {
        repeatList=new ArrayList<CheckBox>();
        mDay1 = (CheckBox) findViewById(R.id.cb_day_1);
        mDay2 = (CheckBox) findViewById(R.id.cb_day_2);
        mDay3 = (CheckBox) findViewById(R.id.cb_day_3);
        mDay4 = (CheckBox) findViewById(R.id.cb_day_4);
        mDay5 = (CheckBox) findViewById(R.id.cb_day_5);
        mDay6 = (CheckBox) findViewById(R.id.cb_day_6);
        mDay7 = (CheckBox) findViewById(R.id.cb_day_7);
        repeatList.add(mDay1);
        repeatList.add(mDay2);
        repeatList.add(mDay3);
        repeatList.add(mDay4);
        repeatList.add(mDay5);
        repeatList.add(mDay6);
        repeatList.add(mDay7);
    }

    private void initBottom() {
        mCancel = (ImageView) findViewById(R.id.ivCancel);
        mDone = (ImageView) findViewById(R.id.ivDone);
    }


    private void initData() {
        mTimePicker.setCurrentHour(mHours);
        mTimePicker.setCurrentMinute(mMinute);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_alarm,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_done:
                doneAlarm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取TimePicker的时间
    private void initListener() {

        mToolbarNavigation.setOnClickListener(v -> onBackPressed());
        mTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            mMinute = minute;
            mHours = hourOfDay;
        });
        mLazyLevel.setOnClickListener(this);
        mRing.setOnClickListener(this);
        mTag.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mDone.setOnClickListener(this);
    }
    //点击监听
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aiv_tag_add:
                showTagDialog();
                break;
            case R.id.aiv_lazy_add:
                showLazyDialog();
                break;
            case R.id.aiv_ring_add:
                startActivityForResult(new Intent(this, RingSetActivity.class), ConsUtils.ASK_FOR_RING);
                break;
            case R.id.ivCancel:
                cancelAlarm();
                break;
            case R.id.ivDone:
                LogUtils.d("新建闹钟完成");
                doneAlarm();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case ConsUtils.RING_SET_CANCEL:
                break;
            case ConsUtils.RING_SET_DONG:
                RingName=data.getStringExtra("songname");
                if(data.getStringExtra("songid")!=null){
                    Ringid=data.getStringExtra("songid");
                }
                mRing.setDesc(RingName);
                break;
        }
    }

    private int[] getRepeatDay() {
        //当用户点击完成时 判断各个CheckBox的勾选情况
        String dayRepeat="";
        if(mDay1.isChecked()){
            dayRepeat+="1"+",";
        }
        if(mDay2.isChecked()){
            dayRepeat+="2"+",";
        }
        if(mDay3.isChecked()){
            dayRepeat+="3"+",";
        }
        if(mDay4.isChecked()){
            dayRepeat+="4"+",";
        }
        if(mDay5.isChecked()){
            dayRepeat+="5"+",";
        }
        if(mDay6.isChecked()){
            dayRepeat+="6"+",";
        }
        if(mDay7.isChecked()){
            dayRepeat+="7"+",";
        }
        if(dayRepeat.equals("")){
            dayRepeat="0,";
        }
        return AlarmInfoDao.getAlarmDayofWeek(dayRepeat);
    }
    public AlarmInfo getAddAlarmInfo(){
        AlarmInfo alarmInfo=new AlarmInfo();
        alarmInfo.setHour(mHours);
        alarmInfo.setMinute(mMinute);

        int[] day=getRepeatDay();
        alarmInfo.setDayOfWeek(day);
        alarmInfo.setLazyLevel(mLevel);
        LogUtils.d("alarm", "加入是的铃声名字" +RingName);
        alarmInfo.setTag(mTagDesc);
        alarmInfo.setRing(RingName);
        alarmInfo.setRingResId(Ringid);
        return alarmInfo;
    }

    //底边栏的两个方法
    private void doneAlarm(){
        //当用户完成设置时，将时间封装到对象中，传回给AlarmhomeActivity
        AlarmInfoDao dao=new AlarmInfoDao(this);
        AlarmInfo alarmInfo;
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        if(mIntent.getBooleanExtra("update", false)){

            alarmInfo=getAddAlarmInfo();
            intent.setClass(this, AlarmHomeActivity.class);
            bundle.putSerializable("alarm", alarmInfo);
            intent.putExtras(bundle);
            LogUtils.d("alarm", "修改闹钟" + Arrays.toString(alarmInfo.getDayOfWeek()));
            //取消旧的闹钟
            AlarmInfo oldAlarmInfo=dao.findById(mIntent.getStringExtra("oldId"));
            AlarmClock alarmClock=new AlarmClock(this);
            alarmClock.turnAlarm(oldAlarmInfo,null,false);
            //修改数据库
            dao.updateAlarm(mIntent.getStringExtra("oldId"), alarmInfo);
            setResult(ConsUtils.UPDATE_ALARM_DONE,intent);
        }else{
            alarmInfo=getAddAlarmInfo();
            dao.addAlarmInfo(alarmInfo);
            bundle.putSerializable("alarm", alarmInfo);
            intent.putExtras(bundle);
            LogUtils.d("alarm", "添加闹钟" + Arrays.toString(alarmInfo.getDayOfWeek()));
            setResult(ConsUtils.SET_ALARM_DONE, intent);
        }

        finish();
    }

    private void cancelAlarm(){
        if(mIntent.getStringExtra("oldId")!=null){
            setResult(ConsUtils.UPDATE_ALARM_CANCEL,new Intent());
            startActivity(new Intent(this,AlarmHomeActivity.class));
        }
        else{
            setResult(ConsUtils.SET_ALARM_CANCEL, new Intent());
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelAlarm();
    }

    //选择赖床级数
    private void showLazyDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("选择你的赖床指数");
        String[] item=new String[]{"本宝宝从不赖床！","稍微拖延个七八分钟啦~"
                ,"半个小时准时起床！","七点的闹钟八点起~","闹钟是什么东西？！"};
        dialog.setSingleChoiceItems(item, 0, (dialog1, which) -> {
            mLevel=which;
            mLazyLevel.setDesc("赖床指数:"+mLevel+"级");
            dialog1.dismiss();
        });
        dialog.show();
    }
    //编辑标签
    private void showTagDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        AlertDialog dialog=builder.create();
        View edit=View.inflate(this,R.layout.dialog_tag,null);
        final EditText tag= (EditText) edit.findViewById(R.id.et_tag);
      /*  dialog.setTitle();
        dialog.setView(edit);*/
        builder.setTitle("闹钟标签");
        builder.setView(edit);
        builder.setPositiveButton("完成", (dialog, which) -> {
            mTagDesc=tag.getText().toString();
            mTag.setDesc(mTagDesc);
            dialog.dismiss();
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void setToolbarTitle(String title) {
        mToolbarTitle.setText(title);
    }

}
