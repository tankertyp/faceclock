package com.pingfly.faceclock.ui.view;


import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqr.recyclerview.LQRRecyclerView;

public interface IAlarmClockModeAtView {

    TextView getTvSelectAll();

    TextView getTvSelectAllNot();

    ImageView getFabAlarmClockNew();

    TextView getTvEmpty();

    LQRRecyclerView getRvAlarmClock();

    //PopupWindow getPopupWindowDelete();

}
