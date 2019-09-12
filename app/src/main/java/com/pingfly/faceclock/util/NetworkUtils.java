package com.pingfly.faceclock.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.alarmclock.util.PrefUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @描述 跟网络相关的工具类
 */
public class NetworkUtils {

    private NetworkUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断当前网络是否可以上网并吐司提醒
     */
    public static boolean isConnectedAndToast(Context context) {
        boolean flag = isNetworkAvailable(context);
        if (!flag) {
            UIUtils.showToast("请检查网络状态");
        }
        return flag;
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(AppCompatActivity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    public static boolean isInternetAvilable(Context context) {
        Boolean isOn=false;
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo net=cm.getActiveNetworkInfo();
        if(net!=null){
            isOn=cm.getActiveNetworkInfo().isAvailable();
        }
        return isOn;
    }

    //从本地读取缓存
    public static void getWeatherInfoFromLocal(Activity mActivity,Class<? extends Object> classOfT) {
        File file=new File(mActivity.getCacheDir(),"wether.json");
        //如果本地有缓存，从本地读取
        if(file.exists()){
            BufferedReader br=null;
            try {
                br=new BufferedReader(new FileReader(file));
                StringBuffer sb=new StringBuffer();
                String read;
                while((read=br.readLine())!=null){
                    sb.append(read);
                }
                parseData(mActivity,sb.toString(),classOfT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void getWeatherInfoFromServer(final Activity mActivity, final Class<? extends Object> classOfT) {

        new Thread(){
            @Override
            public void run() {
                String result=WeatherUtils.getRequest1("武汉");

                //拿到数据后缓存到本地；并记录时间
                if(result!=null)
                    saveDataToLocal(mActivity,result);
                PrefUtils.putlong(mActivity,ConsUtils.Last_REQUEST_TIME,System.currentTimeMillis());
            }
        }.start();

    }

    public static Object parseData(Activity activity,String result,Class<? extends Object> classOfT) {
        if (result == null) {
            //Toast.makeText(activity, "请求天气数据出错，请检查网络", Toast.LENGTH_SHORT).show();
            UIUtils.showToast(UIUtils.getString(R.string.weather_request_fail));
            return null;
        }
        Gson gson = new Gson();

        return gson.fromJson(result,classOfT);
    }

    public static void saveDataToLocal(Activity activity, String result) {
        File file=new File(activity.getCacheDir(),"wether.json");
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
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
