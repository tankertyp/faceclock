package com.pingfly.faceclock.ui.activity;

import android.app.Activity;
import android.widget.Button;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;

import butterknife.BindView;


public class RecordDeleteActivity extends BaseActivity {

    @BindView(R.id.btnCancel)
    Button mBtnCancel;    // 取消按钮
    @BindView(R.id.btnConfirm)
    Button mBtnConfirm;    // 确认按钮

    @Override
    public void initListener() {
        mBtnConfirm.setOnClickListener(v -> {
           setResult(Activity.RESULT_OK, getIntent());
           finish();
        });
        mBtnCancel.setOnClickListener(v -> finish());
    }



    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_record_delete;
    }

}
