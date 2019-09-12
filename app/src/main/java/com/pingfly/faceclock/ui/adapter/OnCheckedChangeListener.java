package com.pingfly.faceclock.ui.adapter;

import android.widget.CompoundButton;

public interface OnCheckedChangeListener {
    void onCheckChanged(MyViewHolder helper, CompoundButton compoundButton,boolean isChecked, int position);
}
