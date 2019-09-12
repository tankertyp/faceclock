package com.pingfly.faceclock.bluetooth.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;

import butterknife.BindView;

public class SendTxtViewHolder extends BaseViewHolder {

    @BindView(R.id.sendTxtTv)
    TextView sendTxtTv;

    public SendTxtViewHolder(ViewGroup root) {
        super(root, R.layout.item_send_txt);
    }


    @Override
    public void bindData(Object o) {
        String message = (String) o;
        sendTxtTv.setText(message);
    }

}
