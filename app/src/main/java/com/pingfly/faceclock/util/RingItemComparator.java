package com.pingfly.faceclock.util;

import com.pingfly.faceclock.app.AppConst;

import java.util.Comparator;
import java.util.Map;

public class RingItemComparator implements Comparator<Map<String, String>> {
    @Override
    public int compare(Map<String, String> o1, Map<String, String> o2) {
        String name1 = o1.get(AppConst.RING_NAME);
        String name2 = o2.get(AppConst.RING_NAME);
        return name1.compareToIgnoreCase(name2);
    }

}
