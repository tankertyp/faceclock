package com.pingfly.faceclock.util;

import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.RecordDeleteItem;

import java.util.Comparator;
import java.util.Map;

public class RecordItemDeleteComparator implements Comparator<RecordDeleteItem> {
    @Override
    public int compare(RecordDeleteItem o1, RecordDeleteItem o2) {
        String name1 = o1.getRingName();
        String name2 = o2.getRingName();
        return name1.compareToIgnoreCase(name2);
    }

}
