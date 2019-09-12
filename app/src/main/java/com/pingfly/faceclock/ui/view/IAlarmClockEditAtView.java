package com.pingfly.faceclock.ui.view;

import android.widget.SeekBar;
import android.widget.TextView;

import com.lqr.optionitemview.OptionItemView;

public interface IAlarmClockEditAtView {

    TextView getTvTimePicker();

    TextView getTvRepeatDescribe();

    SeekBar getSbVolume();

}
