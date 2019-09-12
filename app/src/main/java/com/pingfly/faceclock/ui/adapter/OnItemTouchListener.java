package com.pingfly.faceclock.ui.adapter;

import android.view.MotionEvent;
import android.view.View;

public interface OnItemTouchListener {
    boolean onItemTouch(MyViewHolder helper, View childView, MotionEvent event, int position);
}
