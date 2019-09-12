package com.pingfly.faceclock.util;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.broadcast.AlarmClockBroadcast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

import static com.kongzue.dialog.v2.InputDialog.dip2px;

// 对于一个项目来说，项目中将多次用到sharedPreferences共享参数，去存储用户的登录状态或清除登录状态

/**
 * 1.修改头像
 * 2.修改昵称
 * 3.修改手机号码
 * 4.修改登录密码
 *
 *
 *
 */

public class MyUtils {

    /**
     * Log tag ：MyUtil
     */
    private static final String TAG = MyUtils.class.getSimpleName();
    private static final String LOG_TAG = "MyUtils";

    // 读取用户名
    public static String readLoginUserName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        String userName=sharedPreferences.getString("loginUserName","");
        return userName;
    }

    //读取登录状态
    public static boolean readLoginStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        return isLogin;
    }

    //清除登录状态
    public static void cleanLoginStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",false);
        editor.putString("loginUserName","");
        editor.apply();
    }

    /**
     * 开启闹钟
     *
     * @param context    context
     * @param alarmClock 闹钟实例
     */
    @TargetApi(27)
    public static void startAlarmClock(Context context, com.pingfly.faceclock.bean.AlarmClock alarmClock) {
//        Intent intent = new Intent("com.kaku.weac.broadcast.alarm_clock_ONTIME");
        Intent intent = new Intent(context, AlarmClockBroadcast.class);
        intent.putExtra(AppConst.ALARM_CLOCK, alarmClock);
        // FLAG_UPDATE_CURRENT：如果PendingIntent已经存在，保留它并且只替换它的extra数据。
        // FLAG_CANCEL_CURRENT：如果PendingIntent已经存在，那么当前的PendingIntent会取消掉，然后产生一个新的PendingIntent。
        PendingIntent pi = PendingIntent.getBroadcast(context,
                alarmClock.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager AlarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        // 取得下次响铃时间
        long nextTime = calculateNextTime(alarmClock.getHour(),
                alarmClock.getMinute(), alarmClock.getWeeks());
        // 设置闹钟
        // 当前版本为27或以上使用精准闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AlarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime, pi);
        } else {
            AlarmManager.set(AlarmManager.RTC_WAKEUP, nextTime, pi);
        }

    }



    /**
     * 取消闹钟
     *
     * @param context        context
     * @param alarmClockCode 闹钟启动code
     */
    public static void cancelAlarmClock(Context context, int alarmClockCode) {
        Intent intent = new Intent(context, AlarmClockBroadcast.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, alarmClockCode,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(AppCompatActivity.ALARM_SERVICE);
        assert am != null;
        am.cancel(pi);
    }




    /**
     * 取得下次响铃时间
     *
     * @param hour   小时
     * @param minute 分钟
     * @param weeks  周
     * @return 下次响铃时间
     */
    public static long calculateNextTime(int hour, int minute, String weeks) {
        // 当前系统时间
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 下次响铃时间
        long nextTime = calendar.getTimeInMillis();
        // 当单次响铃时
        if (weeks == null) {
            // 当设置时间大于系统时间时
            if (nextTime > now) {
                return nextTime;
            } else {
                // 设置的时间加一天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                nextTime = calendar.getTimeInMillis();
                return nextTime;
            }
        } else {
            nextTime = 0;
            // 临时比较用响铃时间
            long tempTime;
            // 取得响铃重复周期
            final String[] weeksValue = weeks.split(",");
            for (String aWeeksValue : weeksValue) {
                int week = Integer.parseInt(aWeeksValue);
                // 设置重复的周
                calendar.set(Calendar.DAY_OF_WEEK, week);
                tempTime = calendar.getTimeInMillis();
                // 当设置时间小于等于当前系统时间时
                if (tempTime <= now) {
                    // 设置时间加7天
                    tempTime += AlarmManager.INTERVAL_DAY * 7;
                }

                if (nextTime == 0) {
                    nextTime = tempTime;
                } else {
                    // 比较取得最小时间为下次响铃时间
                    nextTime = Math.min(tempTime, nextTime);
                }

            }

            return nextTime;
        }
    }

    public static void mkdirs(String filePath) {
        boolean mk = new File(filePath).mkdirs();
        LogUtils.d(TAG, "mkdirs: " + mk);
    }



    /**
     * 转换文件大小
     *
     * @param fileLength file
     * @param pattern    匹配模板 "#.00","0.0"...
     * @return 格式化后的大小
     */
    public static String formatFileSize(long fileLength, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        String fileSizeString;
        if (fileLength < 1024) {
//            fileSizeString = df.format((double) fileLength) + "B";
            fileSizeString = "0KB";
        } else if (fileLength < 1048576) {
            fileSizeString = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            fileSizeString = df.format((double) fileLength / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileLength / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /***
     * 转换文件时长
     *
     * @param ms 毫秒数
     * @return mm:ss
     */
    public static String formatFileDuration(int ms) {
        // 单位秒
        int ss = 1000;
        // 单位分
        int mm = ss * 60;

        // 剩余分钟
        int remainMinute = ms / mm;
        // 剩余秒
        int remainSecond = (ms - remainMinute * mm) / ss;

        return addZero(remainMinute) + ":"
                + addZero(remainSecond);

    }

    /**
     * 格式化时间
     *
     * @param hour   小时
     * @param minute 分钟
     * @return 格式化后的时间:[xx:xx]
     */
    public static String formatTime(int hour, int minute) {
        return addZero(hour) + ":" + addZero(minute);
    }



    /**
     * 时间补零
     *
     * @param time 需要补零的时间
     * @return 补零后的时间
     */
    public static String addZero(int time) {
        if (String.valueOf(time).length() == 1) {
            return "0" + time;
        }

        return String.valueOf(time);
    }

    /**
     * 振动单次100毫秒
     *
     * @param context context
     */
    public static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }


    /**
     * 去掉文件扩展名
     *
     * @param fileName 文件名
     * @return 没有扩展名的文件名
     */
    public static String removeEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < fileName.length())) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }




    private static long mLastClickTime = 0;             // 按钮最后一次点击时间
    private static final int SPACE_TIME = 500;          // 空闲时间

    /**
     * 是否连续点击按钮多次
     *
     * @return 是否快速多次点击
     */
    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - mLastClickTime <= SPACE_TIME) {
            return true;
        } else {
            mLastClickTime = time;
            return false;
        }
    }




    /**
     * Returns specified directory(/mnt/sdcard/Android/data/<application package>/files/...).
     * directory will be created on SD card by defined path if card
     * is mounted. Else - Android defines files directory on device's
     * files（/data/data/<application package>/files） system.
     *
     * @param context context
     * @param path    file  path (e.g.: "/music/a.mp3", "/pictures/a.jpg")
     * @return File {@link File directory}
     */
    public static File getExternalFileDirectory(Context context, String path) {
        File file = null;
        if (isHasSDCard()) {
            file = new File(context.getExternalFilesDir(null), path);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    file = null;
                }
            }
        }
        if (file == null) {
            // 使用内部缓存[MediaStore.EXTRA_OUTPUT ("output")]是无法正确写入裁切后的图片的。
            // 系统是用全局的ContentResolver来做这个过程的文件io操作，app内部的存储被忽略。（猜测）
            file = new File(context.getFilesDir(), path);
        }
        return file;
    }



    public static boolean isHasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Returns directory absolutePath.
     *
     * @param context context
     * @param path    file path (e.g.: "/AppDir/a.mp3", "/AppDir/files/images/a.jpg")
     * @return /mnt/sdcard/Android/data/<application package>/files/....
     */
    public static String getFilePath(Context context, String path) {
        return getExternalFileDirectory(context, path).getAbsolutePath();
    }

    /**
     * set intent options
     *
     * @param context  context
     * @param uri      image path uri
     * @param filePath save path (e.g.: "/AppDir/a.mp3", "/AppDir/files/images/a.jpg")
     * @param type     0，截取壁纸/拍照；1，截取Logo
     * @return Intent
     */
    public static Intent getCropImageOptions(Context context, Uri uri, String filePath, int type) {
        int width;
        int height;
        // 截取壁纸/拍照
        if (type == 0) {
            width = context.getResources().getDisplayMetrics().widthPixels;
            height = context.getResources().getDisplayMetrics().heightPixels;
        } else { // 截取logo
            width = height = dip2px(context, 30);
        }

        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        // 保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getExternalFileDirectory
                (context, filePath)));
        // 是否去除面部检测
        intent.putExtra("noFaceDetection", true);
        // 是否保留比例
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 裁剪区的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        // 是否将数据保留在Bitmap中返回
        intent.putExtra("return-data", false);
        return intent;
    }



    /**
     * dp转px
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,getDisplayMetrics(context));
    }

    /**
     * sp转px
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,getDisplayMetrics(context));
    }

    /**
     * px转dp
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2dp(Context context, float pxVal){
        return (int) (pxVal / getDisplayMetrics(context).density + 0.5f);
    }

    /**
     * px转sp
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2sp(Context context, float pxVal){
        return (int) (pxVal / getDisplayMetrics(context).scaledDensity + 0.5f);
    }

    /**
     * 获取DisplayMetrics
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取应用版本号
     *
     * @param context context
     * @return 版本号
     */
    public static String getVersion(Context context) {
        String version;
        try {
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(LOG_TAG, "assignViews: " + e.toString());
            version = context.getString(R.string.version);
        }
        return version;
    }
}
