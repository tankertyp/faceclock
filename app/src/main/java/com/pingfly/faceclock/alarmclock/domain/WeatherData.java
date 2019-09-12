package com.pingfly.faceclock.alarmclock.domain;

import java.util.ArrayList;

public class WeatherData {
    public String reason;
    public WeatherResultData result;
    public int error_code;

    @Override
    public String toString() {
        return "WeatherData{" +
                "reason='" + reason + '\'' +
                ", error_code=" + error_code +
                '}';
    }

    public class WeatherResultData{
        public WeatherDataData data;
    }
    //data
    public class WeatherDataData{
        public TodayData realtime;
        public Life life;
        public PM25 pm25;
        public ArrayList<FutureWeather> weather;
    }
    //当前的各种天气状态
    public class TodayData{
        public Wind wind;
        public TodayWeather weather;
    }
    //TodayData风向
    public class Wind{
        public String direct;//风向
        public String power;//级数
    }
    //TodayData天气
    public class TodayWeather{
        public String info;//晴 多云等等
        public String temperature;//温度
        public String img;//图片码
    }


    //生活指数
    public class Life{
        public String date;
        public LifeInfo info;
    }
    //Life
    public class LifeInfo{
        public ArrayList<String> yundong;//运动
        public ArrayList<String> ziwaixian;//紫外线
        public ArrayList<String> ganmao;//感冒
        public ArrayList<String> wuran;//污染
        public ArrayList<String> chuanyi;//穿衣
    }
    //PM2.5
    public class PM25{
        public PM25Info pm25;

    }
    //PM25
    public class PM25Info {
        public String pm25;//pm2.5
        public String quality;//污染情况
        public String des;//描述
    }

    public class FutureWeather{
        public FutureInfo info;
    }
    public class FutureInfo{
        public ArrayList<String> day;//0位置是图片
    }
}
