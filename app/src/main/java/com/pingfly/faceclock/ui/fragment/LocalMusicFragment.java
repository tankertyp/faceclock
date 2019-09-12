package com.pingfly.faceclock.ui.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bean.RingSelectItem;
import com.pingfly.faceclock.ui.activity.RingSelectActivity;
import com.pingfly.faceclock.ui.adapter.RingSelectAdapter;
import com.pingfly.faceclock.ui.base.BaseListFragment;
import com.pingfly.faceclock.util.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;



/**
 * 显示本地音乐列表的Fragment
 *
 */
public class LocalMusicFragment extends BaseListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * 保存铃声信息的Adapter
     */
    RingSelectAdapter mLocalMusicAdapter;

    /**
     * loader Id
     */
    private static final int LOADER_ID = 1;

    /**
     * 铃声选择位置
     */
    private int mPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ring_local_music, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 管理cursor
        LoaderManager loaderManager = getLoaderManager();
        // 注册Loader
        loaderManager.initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Map<String, String> map = mLocalMusicAdapter.getItem(position);
        // 取得铃声名
        String ringName = Objects.requireNonNull(map).get(AppConst.RING_NAME);
        // 取得播放地址
        String ringUrl = map.get(AppConst.RING_URL);
        // 更新当前铃声选中的位置
        mLocalMusicAdapter.updateSelection(ringName);
        // 更新适配器刷新铃声列表显示
        mLocalMusicAdapter.notifyDataSetChanged();
        // 设置最后一次选中的铃声选择界面位置为本地音乐界面
        RingSelectItem.getInstance().setRingPager(1);

        // 播放音频文件
        //AudioPlayer.getInstance(getActivity()).play(ringUrl, false, false);

        ViewPager pager = (ViewPager) Objects.requireNonNull(getActivity()).findViewById(R.id.fragment_ring_select_sort);
        PagerAdapter f = pager.getAdapter();
        SystemRingFragment systemRingFragment = (SystemRingFragment) Objects.requireNonNull(f).instantiateItem(pager, 0);
        NanopiMusicFragment nanopiMusicFragment = (NanopiMusicFragment) f.instantiateItem(pager, 2);
        // 取消系统铃声选中标记
        if (systemRingFragment.mSystemRingAdapter != null) {
            systemRingFragment.mSystemRingAdapter.updateSelection("");
            systemRingFragment.mSystemRingAdapter.notifyDataSetChanged();
        }

        // 取消nanopi music选中标记
        if (nanopiMusicFragment.mNanopiMusicAdapter != null) {
            nanopiMusicFragment.mNanopiMusicAdapter.updateSelection("");
            nanopiMusicFragment.mNanopiMusicAdapter.notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // 查询外部存储音频文件
        return new CursorLoader(Objects.requireNonNull(getActivity()),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA}, null, null,
                MediaStore.Audio.Media.DISPLAY_NAME);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ID:
                // 当为编辑闹钟状态时，铃声名为闹钟单例铃声名
                String ringName1;
                if (RingSelectActivity.sRingName != null) {
                    ringName1 = RingSelectActivity.sRingName;
                } else {
                    SharedPreferences share = Objects.requireNonNull(getActivity()).getSharedPreferences(
                            AppConst.EXTRA_FACECLOCK_SHARE, AppCompatActivity.MODE_PRIVATE);
                    // 当为新建闹钟状态时，铃声名为最近一次选择保存的铃声名
                    ringName1 = share.getString(AppConst.RING_NAME, "");
                }
                // 保存铃声信息的List
                List<Map<String, String>> list = new ArrayList<>();
                // 过滤重复音频文件的Set
                HashSet<String> set = new HashSet<>();

                if (cursor != null) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                            .moveToNext()) {
                        // 音频文件名
                        String ringName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        // 取得音频文件的地址
                        String ringUrl = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA));
                        if (ringName != null) {
                            // 当过滤集合里不存在此音频文件，并且文件扩展名不为[.amr]，并且不是默认铃声
                            if (!set.contains(ringName)
                                    && !ringUrl.contains("/FaceClock/audio/record")
                                    && !ringName.equals("record_start.mp3")
                                    && !ringName.equals("record_stop.mp3")
                                    && !ringName
                                    .equals("everyday.mp3")) {
                                // 添加音频文件到列表过滤同名文件
                                set.add(ringName);
                                // 去掉音频文件的扩展名
                                ringName = MyUtils.removeEx(ringName);
                                Map<String, String> map = new HashMap<>();
                                map.put(AppConst.RING_NAME, ringName);
                                map.put(AppConst.RING_URL, ringUrl);
                                list.add(map);
                                // 当列表中存在与保存的铃声名一致时，设置该列表的显示位置
                                if (ringName.equals(ringName1)) {
                                    mPosition = list.size() - 1;
                                    RingSelectItem.getInstance().setRingPager(1);
                                }
                            }
                        }
                    }
                }

                mLocalMusicAdapter = new RingSelectAdapter(getActivity(), list, ringName1);
                setListAdapter(mLocalMusicAdapter);
                setSelection(mPosition);
                break;
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> arg0) {

    }

}
