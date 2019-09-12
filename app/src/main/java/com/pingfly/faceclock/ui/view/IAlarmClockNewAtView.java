package com.pingfly.faceclock.ui.view;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lqr.optionitemview.OptionItemView;

public interface IAlarmClockNewAtView {

    TextView getTvTimePicker();

    TextView getTvRepeatDescribe();

    SeekBar getSbVolume();

    OptionItemView getOivRingDescribe();

}
