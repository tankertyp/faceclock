package com.pingfly.faceclock.ui.fragment;


import android.widget.Button;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.activity.AddDeviceActivity;
import com.pingfly.faceclock.ui.activity.AddDeviceActivity2;
import com.pingfly.faceclock.ui.activity.AlarmClockModeActivity;
import com.pingfly.faceclock.ui.activity.AlarmHomeActivity;
import com.pingfly.faceclock.ui.activity.MainActivity;
import com.pingfly.faceclock.ui.activity.MusicModeActivity;
import com.pingfly.faceclock.ui.activity.UploadFaceActivity;
import com.pingfly.faceclock.ui.base.BaseFragment;
import com.pingfly.faceclock.ui.presenter.HomeFgPresenter;
import com.pingfly.faceclock.ui.view.IHomeFgView;

import butterknife.BindView;

public class HomeFragment extends BaseFragment<IHomeFgView, HomeFgPresenter>
        implements IHomeFgView{
    @BindView(R.id.btnAddDevice)
    Button mBtnAddDevice;
    @BindView(R.id.btnUploadFace)
    Button mBtnUploadFace;
    @BindView(R.id.btnMusicMode)
    Button mBtnMusicMode;
    @BindView(R.id.btnAlarmClockMode)
    Button mBtnAlarmClockMode;



    @Override
    public void initListener() {
        mBtnAddDevice.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(AddDeviceActivity.class));
        mBtnUploadFace.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(UploadFaceActivity.class));
        mBtnMusicMode.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(MusicModeActivity.class));
        //mBtnAlarmClockMode.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(AlarmClockModeActivity.class));
        mBtnAlarmClockMode.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(AlarmHomeActivity.class));
    }



    @Override
    protected HomeFgPresenter createPresenter() {
        return new HomeFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_home;
    }

}
