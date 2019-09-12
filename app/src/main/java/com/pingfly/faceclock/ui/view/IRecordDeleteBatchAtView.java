package com.pingfly.faceclock.ui.view;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.recyclerview.LQRRecyclerView;

public interface IRecordDeleteBatchAtView {

    TextView getTvTitle();

    ImageView getBtnCancel();

    TextView getBtnSelectNone();

    TextView getBtnSelectAll();

    Button getBtnDelete();

    LQRRecyclerView getRvRecord();
}
