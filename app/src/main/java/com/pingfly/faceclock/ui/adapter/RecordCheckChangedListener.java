package com.pingfly.faceclock.ui.adapter;

import com.pingfly.faceclock.bean.RecordDeleteItem;

/**
 * 批量录音删除选中回调接口
 */
public interface RecordCheckChangedListener {

    /**
     * 选中
     *
     * @param recordDeleteItem 录音删除信息
     */
    void onChecked(RecordDeleteItem recordDeleteItem);

    /**
     * 取消选中
     *
     * @param recordDeleteItem 录音删除信息
     */
    void unChecked(RecordDeleteItem recordDeleteItem);
}

