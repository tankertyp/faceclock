package com.pingfly.faceclock.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * ViewHolder的抽象类(是MyViewHolderForAbsListView和MyViewHolderForRecyclerView的同一父类)
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    protected Context mContext;
    View mConvertView;
    SparseArray<View> mViews;
    private int mMyPosition;//MyViewHolderForAbsListView专用（另一个自带getPosition方法）

    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;
    OnItemTouchListener mOnItemTouchListener;
    //CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;


    public MyViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 根据id得到布局中的View(使用SparseArray保管，提高效率)
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    /**
     * 得到当前item对应的View
     */
    public View getConvertView() {
        return mConvertView;
    }

    public int getMyPosition() {
        return mMyPosition;
    }

    public void setMyPosition(int myPosition) {
        mMyPosition = myPosition;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public OnItemTouchListener getOnItemTouchListener() {
        return mOnItemTouchListener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        mOnItemTouchListener = onItemTouchListener;
    }

    //public CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener() {return mOnCheckedChangeListener;}

    //public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
    //    mOnCheckedChangeListener = onCheckedChangeListener;
    //}


    /*================== 一切有可能的操作控件的方法 begin ==================*/

    /**
     * 设置TextView文字，并返回this
     */
    public MyViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置TextView的文字颜色，并返回this
     */
    public MyViewHolder setTextColor(int viewId, int colorId) {
        TextView tv = getView(viewId);
        tv.setTextColor(mContext.getResources().getColor(colorId));
        return this;
    }

    /**
     * 设置ImageView的图片，并返回this
     */
    public MyViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    /**
     * 设置ImageView的图片，并返回this
     */
    public MyViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置ImageView的图片，并返回this
     */
    public MyViewHolder setImageFileResource(int viewId, String path) {
        ImageView iv = getView(viewId);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        iv.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置背景颜色，并返回this
     */
    public MyViewHolder setBackgroundColor(int viewId, int colorId) {
        View view = getView(viewId);
        view.setBackgroundColor(mContext.getResources().getColor(colorId));
        return this;
    }


    /**
     * 设置背景资源，并返回this
     */
    public MyViewHolder setBackgrounResource(int viewId, int resId) {
        View view = getView(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置显隐，并返回this
     */
    public MyViewHolder setViewVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    /**
     * 设置是否可用，并返回this
     */
    public MyViewHolder setEnabled(int viewId, boolean enabled) {
        View view = getView(viewId);
        view.setEnabled(enabled);
        return this;
    }

    /**
     * 设置是否可获取焦点，并返回this
     */
    public MyViewHolder setFocusable(int viewId, boolean focusable) {
        View view = getView(viewId);
        view.setFocusable(focusable);
        return this;
    }

    /*================== 一切有可能操作控件的方法 end ==================*/

}
