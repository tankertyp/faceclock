package com.pingfly.faceclock.bluetooth.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;
import com.pingfly.faceclock.bluetooth.bean.NewDeviceHeader;

import butterknife.BindView;

public class NewViewHolder extends BaseViewHolder<NewDeviceHeader> {

    @BindView(R.id.headerName)
    TextView headerName;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public NewViewHolder(ViewGroup root) {
        super(root, R.layout.item_new);
    }


    @Override
    public void bindData(NewDeviceHeader newDeviceHeader) {

        if(!newDeviceHeader.getHeaderName().isEmpty()){
            headerName.setText(newDeviceHeader.getHeaderName());
        }

        if(newDeviceHeader.getProgressBarState()){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);
        }
    }

}
