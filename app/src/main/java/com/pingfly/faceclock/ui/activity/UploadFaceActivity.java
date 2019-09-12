package com.pingfly.faceclock.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Button;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.UIUtils;


import java.util.ArrayList;

import butterknife.BindView;

/**
 * 人脸上传
 */
public class UploadFaceActivity extends BaseActivity {

    public static final int REQUEST_IMAGE_PICKER = 1000;

    @BindView(R.id.btNewFace)
    Button mBtNewFace;
    @BindView(R.id.btMyFace)
    Button mBtMyFace;

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.upload_face));
    }

    @Override
    public void initListener() {
        mBtNewFace.setOnClickListener(v ->
            // 从ImageGridActivity中拿到face结果
            //Intent intent = new Intent(this, Camera2FaceActivity.class);
            //startActivityForResult(intent, REQUEST_IMAGE_PICKER);
            jumpToActivity(Camera2FaceActivity.class)
        );
        // mBtMyFace.setOnClickListener(v -> {
        //});
    }

    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            ImageItem imageItem = images.get(0);
                            uploadFace(imageItem);
                        }
                    }
                }
        }
    }
    */


    public void openFolder()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + "/FaceClock/");
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_upload_face;
    }

}

