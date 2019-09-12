package com.pingfly.faceclock.ui.base;



import android.support.v4.app.ListFragment;

import com.pingfly.faceclock.app.LeakCanaryApplication;
import com.squareup.leakcanary.RefWatcher;

import java.util.Objects;

/**
 * Fragment管理类
 *
 */
public class BaseListFragment extends ListFragment {
    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = LeakCanaryApplication.getRefWatcher(Objects.requireNonNull(getActivity()));
        refWatcher.watch(this);
    }
}
