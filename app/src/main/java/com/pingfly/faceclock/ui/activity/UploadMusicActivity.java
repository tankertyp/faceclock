package com.pingfly.faceclock.ui.activity;

import android.bluetooth.BluetoothAdapter;



import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bean.AppMusicInfo;

import java.util.HashMap;
import java.util.List;

// 点击闹钟模式中音乐图标，进行闹钟铃声的选择页面（音乐列表)
public class UploadMusicActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    // UUID必须是Android蓝牙客户端和Nanopi M4一致
    private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";



    private ListView mMusiclist;
    private SimpleAdapter mAdapter;
    List<AppMusicInfo> appmusicInfos = null;
    private List<HashMap<String, Object>> musiclist;
    private HashMap<String, Object> map;



    /**
     * 填充列表
     * @param
     */
    public void setListAdpter(List<HashMap<String, String>> musiclist) {
        mAdapter = new SimpleAdapter(this, musiclist,
                R.layout.appmusic_listview,
                new String[]{"number","title", "Artist", "music_menu"},
                new int[]{R.id.number,R.id.music_title,
                R.id.music_Artist, R.id.music_menu});
        mMusiclist.setAdapter(mAdapter);
    }











}
