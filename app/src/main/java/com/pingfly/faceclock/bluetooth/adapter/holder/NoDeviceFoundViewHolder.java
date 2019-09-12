package com.pingfly.faceclock.bluetooth.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;
import com.pingfly.faceclock.bluetooth.bean.NoDeviceFoundHeader;

import butterknife.BindView;

public class NoDeviceFoundViewHolder extends BaseViewHolder<NoDeviceFoundHeader> {


    @BindView(R.id.headerName)
    TextView headerNameTv;

    public NoDeviceFoundViewHolder(ViewGroup root) {
        super(root, R.layout.item_no_device_found);
    }

    @Override
    public void bindData(NoDeviceFoundHeader noDeviceFoundHeader) {
        if(!noDeviceFoundHeader.getHeaderName().isEmpty()){
            headerNameTv.setText(noDeviceFoundHeader.getHeaderName());
        }
    }
}
