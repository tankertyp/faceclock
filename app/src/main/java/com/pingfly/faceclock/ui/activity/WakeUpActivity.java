package com.pingfly.faceclock.ui.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.alarmclock.domain.WeatherData;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.alarmclock.util.PrefUtils;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.NetworkUtils;
import com.pingfly.faceclock.util.WeatherUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WakeUpActivity extends AppCompatActivity {

    private WeatherData mWeatherData;
    private WeatherData.TodayData mTodayData;//今天的天气情况
    private TextView mDate;
    private TextView mTime;
    private TextView mTemp;
    private TextView mCloth;
    private TextView mYundong;
    private TextView mCold;
    private ImageView mIcon;
    private WeatherData.LifeInfo mLifeData;
    private String date;
    private LinearLayout mNoNetView;
    private LinearLayout mNetView;

    //@BindView(R.id.btnIKnow)
    //Button mBtnIKnow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        ButterKnife.bind(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mDate = (TextView) findViewById(R.id.tv_date_wake);
        mTime = (TextView) findViewById(R.id.tv_time_wake);
        mTemp = (TextView) findViewById(R.id.tv_temp_wake);
        mCloth = (TextView) findViewById(R.id.desc_cloth_wake);
        mYundong = (TextView) findViewById(R.id.desc_yudong_wake);
        mCold = (TextView) findViewById(R.id.desc_cold_wake);
        mIcon = (ImageView) findViewById(R.id.iv_weather_wake);
        mNoNetView = (LinearLayout) findViewById(R.id.ll_nonet_wake);
        mNetView = (LinearLayout) findViewById(R.id.ll_net_wake);


    }


    private void initData() {
        if(NetworkUtils.isInternetAvilable(this)){
            getWeatherInfoFromServer();
        }else{
            showNoDataUI();
        }
    }
    public void initListener() {

        //mBtnIKnow.setOnClickListener(v -> stopRing());


    }

    public void stopRing() {

        finish();
    }

    public void getWeatherInfoFromServer() {

        new Thread(){
            @Override
            public void run() {
                String result=WeatherUtils.getRequest1(PrefUtils.getString(WakeUpActivity.this, ConsUtils.CURRENT_CITY, "北京"));
                parseData(result);
                //拿到数据后缓存到本地；并记录时间
                if(result!=null)
                    LogUtils.d("wake",result);
                saveDataToLocal(result);
            }
        }.start();
    }

    private void saveDataToLocal(String result) {
        File file=new File(this.getCacheDir(),"weather.json");
        FileWriter fw=null;
        BufferedWriter bw=null;
        try {
            if(!file.exists()) file.createNewFile();
            fw=new FileWriter(file);
            bw=new BufferedWriter(fw);
            bw.write(result);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                Objects.requireNonNull(bw).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseData(String result) {
        if (result==null){
            Toast.makeText(this, "请求天气数据出错，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson=new Gson();

        mWeatherData = gson.fromJson(result, WeatherData.class);
        LogUtils.d("weather", mWeatherData.toString());
        if(mWeatherData.error_code==0){
            WeatherData.WeatherResultData weatherResultData=mWeatherData.result;
            WeatherData.WeatherDataData weatherDataData=weatherResultData.data;
            mTodayData = weatherDataData.realtime;
            mLifeData = weatherDataData.life.info;
            date = weatherDataData.life.date;
            //是在子线程中进行的，不能直接更新UI
            handler.sendEmptyMessage(0);
        }else if(mWeatherData.error_code==207302){
            //没有城市信息
            handler.sendEmptyMessage(1);
        }else if(mWeatherData.error_code==207303){
            //网络错误，稍后重试
            handler.sendEmptyMessage(2);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    initUI();
                    break;
                case 1:
                    showNoDataUI();
                    break;
                case 2:
                    showNoDataUI();
                    break;
            }
        }
    };

    private void initUI() {
        Calendar ca=Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        int hour=ca.get(Calendar.HOUR_OF_DAY);
        int minute=ca.get(Calendar.MINUTE);
        String time;
        if(minute<10){
            time=hour+":"+"0"+minute;
        }else{
            time=hour+":"+minute;
        }
        //设置日期和时间
        mDate.setText(date);
        mTime.setText(time);
        //获取气温和图片
        int img=Integer.parseInt(mTodayData.weather.img);
        mTemp.setText(mTodayData.weather.temperature+"°");
        if(img<ConsUtils.WEATHER_IMG_DAY.length){
            mIcon.setImageResource(ConsUtils.WEATHER_IMG_DAY[img]);
        }else{
            mIcon.setImageResource(ConsUtils.WEATHER_IMG_DAY[0]);
        }
        //获取生活指南
        mCloth.setText(mLifeData.chuanyi.get(0)+","+mLifeData.chuanyi.get(1));
        mYundong.setText(mLifeData.yundong.get(0)+","+mLifeData.yundong.get(1));
        mCold.setText(mLifeData.ganmao.get(0)+","+mLifeData.ganmao.get(1));
    }


    public void iKnow(View v){
        Toast.makeText(this,"Good luck today~",Toast.LENGTH_LONG).show();
        //finishAll();
        finish();
    }

    private void showNoDataUI() {
        //没有网络时展现该页面
        mNoNetView.setVisibility(View.VISIBLE);
        mNetView.setVisibility(View.INVISIBLE);
    }

}
