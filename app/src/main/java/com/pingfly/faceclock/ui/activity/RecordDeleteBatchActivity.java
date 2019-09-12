package com.pingfly.faceclock.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.recyclerview.LQRRecyclerView;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.presenter.RecordDeleteBatchAtPresenter;
import com.pingfly.faceclock.ui.view.IRecordDeleteBatchAtView;

import butterknife.BindView;

public class RecordDeleteBatchActivity extends BaseActivity<IRecordDeleteBatchAtView, RecordDeleteBatchAtPresenter>
        implements IRecordDeleteBatchAtView {

    /**
     * 删除requestCode
     */
    private static final int REQUEST_DELETE = 1;


    /**
     * 标题内容格式
     */

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.btnCancel)
    ImageView mBtnCancel;
    @BindView(R.id.btnSelectNone)
    TextView mBtnSelectNone;
    @BindView(R.id.btnSelectAll)
    TextView mBtnSelectAll;
    @BindView(R.id.btnDelete)
    Button mBtnDelete;
    @BindView(R.id.rvRecord)
    LQRRecyclerView mRvRecord;

    @BindView(R.id.record_delete_batch_llyt)
    ViewGroup viewGroup;// 录音批量删除画面


    @Override
    public void initView() {

        mBtnSelectAll.setVisibility(View.VISIBLE);
        mBtnSelectNone.setVisibility(View.GONE);
        mBtnDelete.setClickable(false);

    }

    @Override
    public void initData() {
        mPresenter.loadRecord();
    }

    @Override
    public void initListener() {

        mBtnCancel.setOnClickListener(v -> finish());

        mBtnDelete.setOnClickListener(v -> {
            // 弹出删除确认对话框
            Intent intent = new Intent(this, RecordDeleteActivity.class);
            startActivityForResult(intent, REQUEST_DELETE);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DELETE) {
            mPresenter.deleteRecordSelected();
        }

    }


    @Override
    protected RecordDeleteBatchAtPresenter createPresenter() {
        return new RecordDeleteBatchAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_record_delete_batch;
    }



    @Override
    public TextView getTvTitle() {
        return mTvTitle;
    }

    @Override
    public  ImageView getBtnCancel() {
        return mBtnCancel;
    }

    @Override
    public TextView getBtnSelectNone() {
        return mBtnSelectNone;
    }

    @Override
    public TextView getBtnSelectAll() {
        return mBtnSelectAll;
    }

 @Override
    public Button getBtnDelete() {
        return mBtnDelete;
    }

    @Override
    public  LQRRecyclerView getRvRecord() {
        return mRvRecord;
    }
}

