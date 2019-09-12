package com.pingfly.faceclock.ui.activity;

import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;

import butterknife.BindView;

public class RecordOperateActivity extends BaseActivity {
    @BindView(R.id.tvRecordRename)
    TextView mTvRecordRename;
    @BindView(R.id.tvRecordDelete)
    TextView mTvRecordDelete;
    @BindView(R.id.tvRecordDeleteBatch)
    TextView mTvRecordDeleteBatch;
    @BindView(R.id.tvRecordDetail)
    TextView mTvRecordDetail;


    @Override
    public void initListener() {
        mTvRecordRename.setOnClickListener(v -> getIntent().putExtra(AppConst.TYPE, 1));
        mTvRecordRename.setOnClickListener(v -> getIntent().putExtra(AppConst.TYPE, 2));
        mTvRecordRename.setOnClickListener(v -> getIntent().putExtra(AppConst.TYPE, 3));
        mTvRecordRename.setOnClickListener(v -> getIntent().putExtra(AppConst.TYPE, 4));
    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_record_operate;
    }
}
