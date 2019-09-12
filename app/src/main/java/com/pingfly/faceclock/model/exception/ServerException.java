package com.pingfly.faceclock.model.exception;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.util.UIUtils;

public class ServerException extends Exception {
    public ServerException(int errorCode) {
        this(UIUtils.getString(R.string.error_code) + errorCode);
    }

    public ServerException(String message) {
        super(message);
    }
}
