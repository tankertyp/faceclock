package com.pingfly.faceclock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.alarmclock.domain.AlarmInfo;
import com.pingfly.faceclock.ui.fragment.AlarmFragment;
import com.pingfly.faceclock.alarmclock.service.WakeServiceOne;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.util.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmHomeActivity extends AppCompatActivity {

    @BindView(R.id.tvToolbarTitle)
    public TextView mToolbarTitle;
    @BindView(R.id.ivToolbarNavigation)
    public ImageView mToolbarNavigation;

    private AlarmFragment mFragAlarm;

    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_home);
        ButterKnife.bind(this);
        //判斷是否是新用戶
        //if(PrefUtils.getBoolean(this,ConsUtils.IS_FIRST_TIME,true)){
        //    startActivity(new Intent(this,GuideActivity.class));
        //    finish();
        //    return;
        //}
        //UmengUpdateAgent.update(this);


        //初始化数据库
        initDataBase("china_Province_city_zone.db");
        //initSlideMenu();
        initView();
        initListener();
        initFragment();
        initService();
        //Intent intent=getIntent();
        //if(intent.getBooleanExtra("showGuide", false)){
        //    firstTimeGuide();
        //}
    }

    //拷贝数据库数据
    private void initDataBase(String dbName) {
        File file=new File(getFilesDir(),dbName);
        if(file.exists()){
            return;
        }
        InputStream in=null;
        FileOutputStream out=null;
        try {
            //输入流通过Assets.open获取
            in=getResources().getAssets().open(dbName);

            out=new FileOutputStream(file);
            byte[] buffer=new byte[1024];
            int len=0;
            while((len=in.read(buffer))!=-1){
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                Objects.requireNonNull(in).close();
                Objects.requireNonNull(out).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initService() {
        // 开始service
        startService(new Intent(this, WakeServiceOne.class));
    }


    private void initView() {

        setToolbarTitle(UIUtils.getString(R.string.alarm_clock_mode));
        mFragAlarm = new AlarmFragment();
        //mFragWether = new FragWether();
        //mFragSlideMenu = new FragSlideMenu();
        fab = (FloatingActionButton) findViewById(R.id.iv_add_home);
    }

    private void initListener() {
        mToolbarNavigation.setOnClickListener(v -> onBackPressed());
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft= fm.beginTransaction();
        //加入标记
        //ft.replace(R.id.fl_wether,mFragWether,ConsUtils.FRAG_WETHER);
        //ft.replace(R.id.fl_menu_content,mFragSlideMenu);
        ft.replace(R.id.fl_alam,mFragAlarm);
        ft.commit();
    }


    //添加闹钟
    public void addAlarm(View v){
        startActivityForResult(new Intent(this, AddAlarmActivity.class),
                ConsUtils.ADD_ALARM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ConsUtils.ADD_ALARM:
                switch (resultCode){
                    case ConsUtils.SET_ALARM_DONE:
                        // 关键位置
                        // 通过data获取Bundle
                        Bundle bundle=data.getExtras();
                        AlarmInfo alarmInfo=(AlarmInfo)Objects.requireNonNull(bundle).getSerializable("alarm");

                        mFragAlarm.addAlarmInfo(alarmInfo);
                        break;
                    case ConsUtils.SET_ALARM_CANCEL:
                        break;
                }
            case ConsUtils.UPDATAE_ALARM:
                //FragmentManager fm=getFragmentManager();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                mFragAlarm=new AlarmFragment();
                ft.replace(R.id.fl_alam, mFragAlarm);
                ft.commit();
                break;
            default:

                break;
        }
    }

    public void setToolbarTitle(String title) {
        mToolbarTitle.setText(title);
    }

    @Override
    public void onDestroy() {

        Intent service = new Intent(this, WakeServiceOne.class);
        this.startService(service);

        super.onDestroy();
    }

}
