package com.pingfly.faceclock.alarmclock.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.KeyEvent;


import com.pingfly.faceclock.R;
import com.pingfly.faceclock.alarmclock.receiver.WakeReceiver;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.alarmclock.util.PrefUtils;
import com.pingfly.faceclock.broadcast.VolumeChangeObserver;
import com.pingfly.faceclock.ui.activity.RingSetActivity;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import java.io.IOException;
import java.util.Objects;

public class AlarmRingService  extends Service {

    private static final String TAG = "AlarmRingService";

    private String Song;
    private MediaPlayer mPlayer;
    private Vibrator mVibrator;

    private AudioManager mAudioManager;
    private NoisyAudioStreamReceiver noisyAudioStreamReceiver;
    private VolumeChangeObserver mVolumeChangeObserver;

    private final static int ALARM_INTERVAL = 5 * 60 * 1000;
    private final static int WAKE_REQUEST_CODE = 6666;

    private final static int GRAY_SERVICE_ID = -1001;

    public AlarmRingService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //context = AlarmRingService.this;
        //share.CreateFileWrite("AlarmRingService：onCreate");

        //Daemon.run(DaemonService.this,DaemonService.class, Daemon.INTERVAL_ONE_MINUTE);
        //startTimeTask();
        //grayGuard();


        if(PrefUtils.getBoolean(this, ConsUtils.IS_VIBRATE,false)){
            startVibrate();
        }


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
        // 耳机插拔状态监听
        IntentFilter intentFilter = new  IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyAudioStreamReceiver,intentFilter);


        BluetoothConnectionReceiver audioNoisyReceiver = new BluetoothConnectionReceiver();

        //蓝牙状态广播监听
        IntentFilter audioFilter = new IntentFilter();
        audioFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        audioFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(audioNoisyReceiver, audioFilter);


        /**
        //实例化对象并设置监听器
        mVolumeChangeObserver = new VolumeChangeObserver(this);
        mVolumeChangeObserver.setVolumeChangeListener(this);
        int initVolume = mVolumeChangeObserver.getCurrentMusicVolume();
        LogUtils.e(TAG, "initVolume = " + initVolume);
         **/

    }

    private class NoisyAudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // Pause the playback
                //Toast.makeText(MainActivity.this, "Headset is pluged out", Toast.LENGTH_SHORT).show();
                UIUtils.showToast(UIUtils.getString(R.string.headset_pluged_out));
            }
        }
    }


    /**
     * 蓝牙注册广播
     */
    public class BluetoothConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) {		//蓝牙连接状态
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);

                /*
                if (state == BluetoothAdapter.STATE_CONNECTED || state == BluetoothAdapter.STATE_DISCONNECTED) {
                    //连接或失联，切换音频输出（到蓝牙、或者强制仍然扬声器外放）
                    changeToBtHeadset();
                }
                 */

                if (state == BluetoothAdapter.STATE_CONNECTED ) {
                    //连接
                    changeToBtHeadset();

                    //changeToHeadset();
                }

                if (state == BluetoothAdapter.STATE_DISCONNECTED ) {
                    //失联
                    //changeToSpeaker();

                    changeToHeadset();
                }

            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){	//本地蓝牙打开或关闭
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    //断开，切换音频输出
                    //changeToSpeaker();

                    changeToHeadset();
                }
            }

        }
    }

    public boolean isBlueToothHeadsetConnected()
    {
        boolean retval = true;
        try {
            retval = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(android.bluetooth.BluetoothProfile.HEADSET)
                    != android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

        } catch (Exception exc) {
            // nothing to do
        }
        return retval;
    }



    /**
     * 切换到外放
     */
    public void changeToSpeaker(){
        //注意此处，蓝牙未断开时使用MODE_IN_COMMUNICATION而不是MODE_NORMAL
        mAudioManager.setMode(isBlueToothHeadsetConnected() ? AudioManager.MODE_IN_COMMUNICATION : AudioManager.MODE_NORMAL);
        mAudioManager.stopBluetoothSco();
        mAudioManager.setBluetoothScoOn(false);
        mAudioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到蓝牙音箱
     */
    public void changeToBtHeadset(){
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.startBluetoothSco();
        mAudioManager.setBluetoothScoOn(true);
        mAudioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到耳机模式
     */
    public void changeToHeadset(){
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.stopBluetoothSco();
        mAudioManager.setBluetoothScoOn(false);
        mAudioManager.setSpeakerphoneOn(false);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 报错:java.lang.String android.content.Intent.getStringExtra(java.lang.String)' on a null object reference
        if (intent!=null) {
            Song = intent.getStringExtra("resid");
            if (Song == null) {
                Song = "everybody.mp3";
            }
            ringTheAlarm(Song);
        }

        // 在运行onStartCommand后service进程被kill后，系统将会再次启动service，并传入最后一个intent给onstartCommand。
        // 直到调用stopSelf(int)才停止传递intent。如果在被kill后还有未处理好的intent，那被kill后服务还是会自动启动。因此onstartCommand不会接收到任何null的intent。

        flags = START_STICKY;   //手动返回START_STICKY，亲测当service因内存不足被kill，当内存又有的时候，service又被重新创建
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 响铃函数
     * @param song
     */

    private void ringTheAlarm(String song) {
        AssetFileDescriptor assetFileDescriptor= null;
        try {
            mPlayer=new MediaPlayer();

            mPlayer.reset();

            //mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            if(song.contains("/")){
                //说明是自定义铃声
                mPlayer.setDataSource(song);
            }else{
                assetFileDescriptor = this.getAssets().openFd(song);
                mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            }
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(false); // 设置是否循环播放
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopTheAlarm(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
        }
    }


    private void stopVibrate() {
        if(mVibrator!=null){
            mVibrator.cancel();
        }
    }

    private void startVibrate() {
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(Objects.requireNonNull(mVibrator).hasVibrator()){
            mVibrator.vibrate(new long[]{500, 1500, 500, 1500}, 0);//off on off on
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTheAlarm();
        stopVibrate();
        LogUtils.d("alarm", "铃声关闭");
    }




    private void grayGuard() {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, DaemonInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        //发送唤醒广播来促使挂掉的UI进程重新启动起来
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(WakeReceiver.GRAY_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Objects.requireNonNull(alarmManager).setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, operation);
    }



  //给 API >= 18 的平台上用的灰色保活手段
    public static class DaemonInnerService extends Service {

        @Override
        public void onCreate() {
            LogUtils.i(TAG, "InnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            LogUtils.i(TAG, "InnerService -> onStartCommand");
            startForeground(GRAY_SERVICE_ID, new Notification());
            //stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            LogUtils.i(TAG, "InnerService -> onDestroy");
            super.onDestroy();
        }
    }


}
