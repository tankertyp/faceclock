package com.pingfly.faceclock.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.broadcast.VolumeChangeObserver;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RingSetActivity extends AppCompatActivity implements VolumeChangeObserver.VolumeChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private final static String TAG = RingSetActivity.class.getSimpleName();

    private ListView lv_ring;
    private String[] ringName = new String[]{"Everybody","心如止水", "荆棘鸟", "加勒比海盗", "圣斗士(慎点)",
            "Flower", "Time Traval", "Thank you for", "律动", "Morning", "Echo", "Alarm Clock"};
    private ArrayList<String> ringList;
    private ArrayList<String> ringIDList;
    private String[] songId = new String[]{"everybody.mp3", "peace_of_mind.mp3", "bird.mp3", "galebi.mp3", "shendoushi.mp3",
            "flower.mp3", "timetravel.mp3", "thankufor.mp3", "mx1.mp3", "mx2.mp3", "echo.mp3", "clock.mp3"};
    private int currentItem;
    private MyAdapter mAdapter;
    private MediaPlayer mPlayer;

    private AudioManager mAudioManager;
    private NoisyAudioStreamReceiver noisyAudioStreamReceiver;

    private String setRingName;//最终选定的名字
    private String setRingId;


    private VolumeChangeObserver mVolumeChangeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_set);
        ButterKnife.bind(this);

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


        //实例化对象并设置监听器
        mVolumeChangeObserver = new VolumeChangeObserver(this);
        mVolumeChangeObserver.setVolumeChangeListener(this);
        int initVolume = mVolumeChangeObserver.getCurrentMusicVolume();
        LogUtils.e(TAG, "initVolume = " + initVolume);

        ActionBar ab = getSupportActionBar();
        // 设置返回开启
        //ab.setDisplayHomeAsUpEnabled(true);
        iniView();
        initAdapter();
        initListener();
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
                if (state == BluetoothAdapter.STATE_CONNECTED || state == BluetoothAdapter.STATE_DISCONNECTED) {
                    //连接或失联，切换音频输出（到蓝牙、或者强制仍然扬声器外放）
                    changeToBtHeadset();
                }
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())){	//本地蓝牙打开或关闭
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    //断开，切换音频输出
                    changeToSpeaker();
                    //changeToHeadset();
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
    protected void onResume() {
        //注册广播接收器
        mVolumeChangeObserver.registerReceiver();
        super.onResume();
    }


    @Override
    protected void onPause() {
        //解注册广播接收器
        mVolumeChangeObserver.unregisterReceiver();
        super.onPause();
    }


    @Override
    public void onVolumeChanged(int volume) {
        //系统媒体音量改变时的回调
        LogUtils.e(TAG, "onVolumeChanged()--->volume = " + volume);
    }

/**
    private int getMediaVolume() {

        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void setMediaVolume(int volume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }
**/



    private void iniView() {

        setSupportActionBar(toolbar);
        if( getSupportActionBar() != null)
        getSupportActionBar().setTitle("铃声选择");

        lv_ring = (ListView) findViewById(R.id.lv_ring_set);
        setRingName = "everybody.mp3";
        currentItem = 0;
        setRingId = songId[0];
        LogUtils.d("alarm", "默认得到的id" + setRingId);
        ringList = new ArrayList<String>();
        ringIDList = new ArrayList<String>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ring_set, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done_ring:
                doneRing();
                break;
            case R.id.action_custom_ring:
                startActivityForResult(new Intent(this, CustomRingSetActivity.class), 0);
                stopTheSong();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAdapter() {
        for (int i = 0; i < ringName.length; i++) {
            ringList.add(ringName[i]);
            ringIDList.add(songId[i]);
        }
        mAdapter = new MyAdapter();
        lv_ring.setAdapter(mAdapter);
    }

    private void initListener() {
        lv_ring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setRingName = ringList.get(position);
                setRingId = ringIDList.get(position);
                currentItem = position;
                mAdapter.notifyDataSetChanged();
                /*if(isPlaying){
                    stopTheSong();
                }*/
                ringTheSong(position);
            }
        });
    }

    //播放音乐
    private void ringTheSong(int position) {
        AssetFileDescriptor assetFileDescriptor = null;
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        mPlayer.reset();
        try {
            //mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (position == 0 && !ringList.get(position).equals(ringName[0])) {
                mPlayer.setDataSource(ringIDList.get(0));
            } else {
                assetFileDescriptor = this.getAssets().openFd(ringIDList.get(position));
                mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            }
            //mPlayer.prepareAsync();
            mPlayer.setVolume(0.2f, 0.2f);
            //mPlayer.setVolume(getMediaVolume(), getMediaVolume());
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
            //mPlayer.setVolume(1f, 1f);
        } catch (IOException e) {
            e.printStackTrace();
            //ToastUtils.show("当前歌曲无法播放");
            UIUtils.showToast(UIUtils.getString(R.string.this_ring_cannot_be_played));
        }

        //isPlaying=true;
    }

    private void stopTheSong() {
        if (mPlayer != null) {
            LogUtils.d("ring", "mPlay" + mPlayer.toString());
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }
    }


    static class ViewHolder {
        TextView Name;
        RadioButton Radio;
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(RingSetActivity.this, R.layout.item_ring_set, null);
                holder.Name = (TextView) convertView.findViewById(R.id.tv_name_ring);
                holder.Radio = (RadioButton) convertView.findViewById(R.id.rb_check_ring);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.Name.setText(ringList.get(position));
            if (position == currentItem) {
                holder.Radio.setChecked(true);
            } else {
                holder.Radio.setChecked(false);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return ringList.size();
        }

        @Override
        public Object getItem(int position) {
            return ringList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private void cancelRing() {
        setResult(ConsUtils.RING_SET_CANCEL, new Intent());
        finish();
    }

    private void doneRing() {
        Intent intent = new Intent();
        intent.putExtra("songname", setRingName);
        intent.putExtra("songid", setRingId);
        setResult(ConsUtils.RING_SET_DONG, intent);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mVolumeChangeObserver != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP://增大系统媒体音量
                    mVolumeChangeObserver.raiseMusicVolume();
                    //addMediaVolume(getMediaVolume());
                    LogUtils.d("volume up");
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN://减小系统媒体音量
                    mVolumeChangeObserver.lowerMusicVolume();
                    //cutMediaVolume(getMediaVolume());
                    LogUtils.d("volume down");
                    return true;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
    private void addMediaVolume(int current) {
        current = current + stepVolume;
        if (current >= maxVolume)
            current = maxVolume;
        setMediaVolume(current);
        //volumeSeekBar.setProgress(current);
    }

    private void cutMediaVolume(int current) {
        current = current - stepVolume;
        if (current <= 0)
            current = 0;
        setMediaVolume(current);
        //volumeSeekBar.setProgress(current);
    }
     **/




    @Override
    public void onBackPressed() {
        cancelRing();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(noisyAudioStreamReceiver);
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTheSong();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ringList.add(0, data.getStringExtra("RingName"));
            ringIDList.add(0, data.getStringExtra("RingPath"));
            currentItem = 0;
            setRingId = ringIDList.get(0);
            setRingName = ringList.get(0);
            mAdapter.notifyDataSetChanged();
        }
    }


}
