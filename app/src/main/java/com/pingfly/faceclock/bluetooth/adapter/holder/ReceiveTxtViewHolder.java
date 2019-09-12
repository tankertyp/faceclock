package com.pingfly.faceclock.bluetooth.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;

import butterknife.BindView;

public class ReceiveTxtViewHolder extends BaseViewHolder {

    @BindView(R.id.receiveTxtTv)
    TextView receiveTxtTv;

    public ReceiveTxtViewHolder(ViewGroup root) {
        super(root, R.layout.item_receive_txt);
    }

    @Override
    public void bindData(Object o) {
        String message = (String) o;
        receiveTxtTv.setText(message);
    }



}
