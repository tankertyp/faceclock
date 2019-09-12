package com.pingfly.faceclock.ui.view;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * 需要进行动态显示三个TextView:头像、昵称和账号（手机号）
 */
public interface IMeFgView {

    ImageView getIvHeader();

    TextView getTvName();

    TextView getTvAccount();
}