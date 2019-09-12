package com.pingfly.faceclock.bean;

/**
 * 录音删除信息实例
 */
public class RecordDeleteItem {

    /**
     * 录音地址
     */
    private final String mRingUrl;

    /**
     * 录音名
     */
    private final String mRingName;

    /**
     * 是否选中
     */
    private boolean mIsSelected;

    public RecordDeleteItem(String ringUrl, String ringName, boolean isSelected) {
        super();
        mRingUrl = ringUrl;
        mRingName = ringName;
        mIsSelected = isSelected;
    }

    public String getRingUrl() {
        return mRingUrl;
    }

    public String getRingName() {
        return mRingName;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }
}
