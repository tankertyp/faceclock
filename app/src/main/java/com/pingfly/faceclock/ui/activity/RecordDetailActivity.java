package com.pingfly.faceclock.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.MyUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;



import butterknife.BindView;

public class RecordDetailActivity extends BaseActivity {

    private static final String TAG = "RecordDetailFragment";

    /**
     * 文件名称
     */
    private String mFileName;

    /**
     * 文件大小
     */
    private String mFileSize;

    /**
     * 保存路径
     */
    private String mSavePath;

    /**
     * 修改时间
     */
    private String mModifyTime;

    /**
     * 播放时长
     */
    private String mPlayDuration;

    @BindView(R.id.tvFileName)
    TextView mTvFileName;  // 文件名称TextView
    @BindView(R.id.tvFileFormat)
    TextView mTvFileFormat;  // 文件格式TextView
    @BindView(R.id.tvFileSize)
    TextView mTvFileSize;  // 文件名称TextView
    @BindView(R.id.tvModifyTime)
    TextView mTvModifyTime;  // 修改时间TextView
    @BindView(R.id.tvPlayDuration)
    TextView mTvPlayDuration;  // 播放时长TextView
    @BindView(R.id.tvSavePath)
    TextView mTvSavePath;  // 保存路径TextView
    @BindView(R.id.btnRoger)
    Button mBtnRoger;  // 知道了Button


    @Override
    public void initView() {

        mFileName = getIntent().getStringExtra(
                AppConst.RING_NAME);
        mSavePath = getIntent().getStringExtra(
                AppConst.RING_URL);

        // 文件操作
        File file = new File(mSavePath);
        mFileSize = MyUtils.formatFileSize(file.length(), "0.00");
        long time = file.lastModified();

        mModifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.getDefault()).format(time);

        try {
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(mSavePath);
            player.prepare();
            int ms = player.getDuration();
            mPlayDuration = MyUtils.formatFileDuration(ms);
        } catch (IllegalArgumentException | SecurityException
                | IllegalStateException | IOException e) {
            mPlayDuration = getString(R.string.unknown);
            //LogUtils.e(TAG, "error: " + e.toString());
            LogUtils.e(TAG, "error: " + e.toString());
        }

        mTvFileName.setText(mFileName);
        mTvFileFormat.setText(getString(R.string.amr));
        mTvFileSize.setText(mFileSize);
        mTvModifyTime.setText(mModifyTime);
        mTvPlayDuration.setText(mPlayDuration);
        mTvSavePath.setText(mSavePath);

    }

    @Override
    public void initListener() {

        mBtnRoger.setOnClickListener(v -> finish());

    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_record_detail;
    }

}
