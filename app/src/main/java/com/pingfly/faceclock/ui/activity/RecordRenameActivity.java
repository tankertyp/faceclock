package com.pingfly.faceclock.ui.activity;


import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.UIUtils;

import java.io.File;

import butterknife.BindView;

import static android.view.View.VISIBLE;

public class RecordRenameActivity extends BaseActivity  {


    private static final String TAG = "RecordRenameActivity";

    private String mFileName;   // 文件名称
    private String mRingUrl;    // 录音URL


    @BindView(R.id.btnConfirm)
    Button mBtnConfirm;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.etRename)
    EditText mEtRename;
   @BindView(R.id.tvError)
   TextView mTvError;   // 显示错误信息的TextView

    @Override
    public void initView() {

        mEtRename.setSelection(mEtRename.getText().toString().trim().length());

    }


    @Override
    public void initListener() {
        mBtnConfirm.setOnClickListener(v -> changeRecordName());
        mBtnCancel.setOnClickListener(v -> finish());
        mEtRename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void changeRecordName() {
        showWaitingDialog(UIUtils.getString(R.string.please_wait));
        String rename = mEtRename.getText().toString(); // 取得输入的内容
        // 当输入内容不为空时
        if (!rename.equals("")) {
            // 取得不带扩展名的地址
            String newUrl = mRingUrl
                    .substring(0, mRingUrl.lastIndexOf("/"));
            // 新命名的文件地址
            newUrl += "/" + rename + ".amr";
            File newFile = new File(newUrl);
            // 当重命名文件不存在时
            if (!newFile.exists()) {
                getIntent().putExtra(AppConst.NEW_URL, newUrl);
                setResult(AppCompatActivity.RESULT_OK, getIntent());
                finish();

            } else {
                mTvError.setVisibility(VISIBLE);
                mTvError.setText(getString(R.string.file_name_exist));
            }
        } else {
            // 当输入内容为空时
            mTvError.setVisibility(View.VISIBLE);
            mTvError.setText(getString(R.string.input_empty));
        }

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_record_rename;
    }


}
