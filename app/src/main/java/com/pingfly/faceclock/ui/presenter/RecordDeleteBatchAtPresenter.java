package com.pingfly.faceclock.ui.presenter;

import android.os.Environment;
import android.view.View;
import android.widget.ToggleButton;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.bean.RecordDeleteItem;
import com.pingfly.faceclock.ui.adapter.MyAdapterForRecyclerView;
import com.pingfly.faceclock.ui.adapter.MyHeaderAndFooterAdapter;
import com.pingfly.faceclock.ui.adapter.MyViewHolderForRecyclerView;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.ui.view.IRecordDeleteBatchAtView;
import com.pingfly.faceclock.util.MyUtils;
import com.pingfly.faceclock.util.RecordItemDeleteComparator;
import com.pingfly.faceclock.util.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordDeleteBatchAtPresenter extends BasePresenter<IRecordDeleteBatchAtView> {


    private List<RecordDeleteItem> mData = new ArrayList<>() ;  // 保存录音信息的List
    private List<String> mDeleteList = new ArrayList<>();   // 保存选中删除的录音路径
    private List<RecordDeleteItem> mSelectedData = new ArrayList<>();
    private MyHeaderAndFooterAdapter mAdapter;
    private MyAdapterForRecyclerView<RecordDeleteItem> mSelectedAdapter;



    private String mTitleFormat;
    private int mColorWhite;    // 删除按钮可用文字灰白色
    private int mColorWhiteTrans;   // 删除按钮不可用文字0.6透明白色
    //mColorWhite = getResources().getColor(R.color.white_trans90);
    //mColorWhiteTrans = getResources().getColor(R.color.white_trans60);


    public RecordDeleteBatchAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadRecord() {
        loadData();
        setAdapter();
    }

    private void loadData() {

        if (!MyUtils.isHasSDCard()) {
            UIUtils.showToast(UIUtils.getString(R.string.no_sd_card));
            return;
        }
        // 录音文件路径
        String fileName = getRecordDirectory();
        File file = new File(fileName);
        // 当录音列表不存在时
        if (!file.exists()) {
            return;
        }
        // 列出录音文件夹所有文件
        File[] files = file.listFiles();
        for (File file1 : files) {
            // 音频文件名
            String ringName = file1.getName();
            if (ringName.endsWith(".amr")) {
                // 去掉音频文件的扩展名
                ringName = MyUtils.removeEx(ringName);
                // 取得音频文件的地址
                String ringUrl = file1.getAbsolutePath();
                // 录音删除信息实例
                RecordDeleteItem item = new RecordDeleteItem(ringUrl, ringName,
                        false);
                mData.add(item);
            }
        }
        // 排序铃声列表
        Collections.sort(mData, new RecordItemDeleteComparator());

        setAdapter();
    }

    private void setAdapter() {
        if (mAdapter == null) {
            MyAdapterForRecyclerView adapter = new MyAdapterForRecyclerView<RecordDeleteItem>(mContext, mData, R.layout.item_record_delete_batch) {
                @Override
                public void convert(MyViewHolderForRecyclerView helper, RecordDeleteItem item, int position) {
                    helper.setText(R.id.tvRingName, item.getRingName()).setViewVisibility(R.id.tb, View.VISIBLE);
                    ToggleButton tb = helper.getView(R.id.tb);

                }
            };
            mAdapter = adapter.getHeaderAndFooterAdapter();
            getView().getRvRecord().setAdapter(mAdapter);

            ((MyAdapterForRecyclerView) mAdapter.getInnerAdapter()).setOnItemClickListener((lqrViewHolder, viewGroup, view, i) -> {
                //选中或反选
                RecordDeleteItem recordDeleteItem = mData.get(i - 1);
                if (mSelectedData.contains(recordDeleteItem)) {
                    mSelectedData.remove(recordDeleteItem);
                    mDeleteList.remove(recordDeleteItem.getRingUrl());

                } else {
                    mSelectedData.add(recordDeleteItem);
                    mDeleteList.add(recordDeleteItem.getRingUrl());

                }
                mSelectedAdapter.notifyDataSetChangedWrapper();
                mAdapter.notifyDataSetChanged();

                mTitleFormat = mContext.getString(R.string.selected_xx_item);
                if (  mSelectedData.size() > 0) {
                    getView().getTvTitle().setText(String.format(mTitleFormat, mSelectedData.size()));
                    getView().getBtnDelete().setEnabled(true);
                    getView().getBtnDelete().setBackgroundResource(R.drawable.bg_btn_confirm);

                    if(mSelectedData.size() == mData.size()){
                        getView().getBtnSelectAll().setVisibility(View.GONE);
                        getView().getBtnSelectNone().setVisibility(View.VISIBLE);
                    }
                } else {
                    getView().getBtnDelete().setEnabled(false);
                    getView().getTvTitle().setText(String.format(mTitleFormat, 0));
                }
            });

        };

    }


    /**
     * 设置录音列表List
     */
    private void setRingList() {
        if (!MyUtils.isHasSDCard()) {
            //ToastUtil.showShortToast(getActivity(), getString(R.string.no_sd_card));
            UIUtils.showToast(UIUtils.getString(R.string.no_sd_card));
            return;
        }
        // 录音文件路径
        String fileName = getRecordDirectory();
        File file = new File(fileName);
        // 当录音列表不存在时
        if (!file.exists()) {
            return;
        }
        // 列出录音文件夹所有文件
        File[] files = file.listFiles();
        for (File file1 : files) {
            // 音频文件名
            String ringName = file1.getName();
            if (ringName.endsWith(".amr")) {
                // 去掉音频文件的扩展名
                ringName = MyUtils.removeEx(ringName);
                // 取得音频文件的地址
                String ringUrl = file1.getAbsolutePath();
                // 录音删除信息实例
                RecordDeleteItem item = new RecordDeleteItem(ringUrl, ringName,
                        false);
                mData.add(item);
            }
        }

        // 排序铃声列表
        Collections.sort(mData, new RecordItemDeleteComparator());
    }


    public void deleteRecordSelected() {

        boolean result = false;
        // 遍历删除列表
        int length = mDeleteList.size();
        for (int i = 0; i < length; i++) {
            File file = new File(mDeleteList.get(i));
            // 删除文件
            result = file.delete();
        }
        if (result) {
            //ToastUtil.showShortToast(getActivity(), getString(R.string.delete_success));
            UIUtils.showToast(UIUtils.getString(R.string.delete_success));
        }

        // 清空删除列表
        mDeleteList.clear();
        // 刷新录音列表
        refreshList();

        // 当录音列表为空
        if (mData.size() == 0) {
            // 返回到铃声选择界面
            mContext.finish();
        } else {
            // 设置标题为选中0件
            getView().getTvTitle().setText(String.format(mTitleFormat, 0));
            // 设置删除按钮不可用
            getView().getBtnDelete().setClickable(false);
            getView().getBtnDelete().setBackgroundResource(R.drawable.shape_circle_btn_sure_invalidate);
            getView().getBtnDelete().setTextColor(mColorWhiteTrans);
        }


    }



    /**
     * 更新录音列表显示
     */
    private void refreshList() {
        mData.clear();
        // 设置录音列表
        setRingList();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 取得录音文件路径
     *
     * @return 录音文件路径
     */
    private String getRecordDirectory() {
        // 外部存储根路径
        String fileName = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        // 录音文件路径
        fileName += "/FaceClock/audio/record";
        return fileName;
    }

    /**
     * 解除选中
     *
     * @param recordItem 录音项目实例
     */
    private void onCheck(final RecordDeleteItem recordItem) {
        recordItem.setSelected(true);
        // 添加到录音删除列表
        mDeleteList.add(recordItem.getRingUrl());
        // 录音删除列表已添加的项目个数
        int size = mDeleteList.size();
        // 设置标题显示选中的个数
       getView().getTvTitle().setText(String.format(mTitleFormat, size));

        // 当录音删除个数为1时
        if (size == 1) {
            // 设置删除按钮可用
            getView().getBtnDelete().setClickable(true);
            getView().getBtnDelete().setBackgroundResource(R.drawable.bg_btn_confirm);
            getView().getBtnDelete().setTextColor(mColorWhite);

        }
        // 当录音删除个数为录音列表最大个数时
        if (size == mData.size()) {
            // 隐藏全选按钮，显示取消全选按钮
            getView().getBtnSelectAll().setVisibility(View.GONE);
            getView().getBtnSelectNone().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 选中
     *
     * @param recordItem 录音项目实例
     */
    private void unCheck(final RecordDeleteItem recordItem) {
        recordItem.setSelected(false);
        // 移除录音删除列表
        mDeleteList.remove(recordItem.getRingUrl());
        // 录音删除列表已添加的项目个数
        int size = mDeleteList.size();

        // 设置标题显示选中的个数
        getView().getTvTitle().setText(String.format(mTitleFormat, size));

        // 当录音删除个数为0时
        if (size == 0) {
            // 设置删除按钮不可用
            getView().getBtnDelete().setClickable(false);
            getView().getBtnDelete()
                    .setBackgroundResource(R.drawable.shape_circle_btn_sure_invalidate);
            getView().getBtnDelete().setTextColor(mColorWhiteTrans);

        }

        // 当没有显示全选按钮时
        if ((getView().getBtnSelectAll().getVisibility() == View.GONE)) {
            // 显示全选按钮，隐藏取消全选按钮
            getView().getBtnSelectAll().setVisibility(View.VISIBLE);
            getView().getBtnSelectNone().setVisibility(View.GONE);

        }
    }

}
