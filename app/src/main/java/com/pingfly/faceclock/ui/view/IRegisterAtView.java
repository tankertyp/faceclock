package com.pingfly.faceclock.ui.view;

import android.widget.Button;
import android.widget.EditText;

public interface IRegisterAtView {

    EditText getEtNickName();

    EditText getEtPhone();

    EditText getEtEmail();

    EditText getEtPwd();

    EditText getEtVerifyCode();

    Button getBtnSendCode();

}
