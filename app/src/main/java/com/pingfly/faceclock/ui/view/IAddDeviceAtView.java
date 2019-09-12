package com.pingfly.faceclock.ui.view;

import android.widget.TextView;
import android.widget.ToggleButton;

import com.lqr.recyclerview.LQRRecyclerView;

public interface IAddDeviceAtView {


    ToggleButton getTbBluetooth();

    TextView getTvDiscovery();

    LQRRecyclerView getRvBtDevice();

}
