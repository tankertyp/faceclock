package com.pingfly.faceclock.bluetooth.adapter.holder;



import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;
import com.pingfly.faceclock.bluetooth.base.i.OnItemClickListener;
import com.pingfly.faceclock.bluetooth.bean.Device;


import butterknife.BindView;

public class DeviceViewHolder extends BaseViewHolder<Device> {

    @BindView(R.id.deviceName)
    TextView deviceName;
    @BindView(R.id.deviceAddress)
    TextView deviceAddress;
    @BindView(R.id.deviceState)
    TextView deviceState;

    public DeviceViewHolder(ViewGroup root, OnItemClickListener onItemClickListener) {
        super(root, R.layout.item_device, onItemClickListener);
    }

    @Override
    public void bindData(Device device) {
        deviceAddress.setText(device.getDeviceAddress());
        deviceName.setText(device.getDeviceName());
        if( device.getDeviceState() ){
            deviceState.setVisibility(View.VISIBLE);
        }
    }
}
