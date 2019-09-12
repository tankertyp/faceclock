package com.pingfly.faceclock.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

/**
 * RecyclerView通用的ViewHodler
 */
public class MyViewHolderForRecyclerView extends MyViewHolder {

    public MyViewHolderForRecyclerView(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();

        mConvertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(MyViewHolderForRecyclerView.this, null, v, getPosition());
                }
            }
        });

        mConvertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(MyViewHolderForRecyclerView.this, null, v, getPosition());
                }
                return false;
            }
        });

        mConvertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnItemTouchListener != null) {
                    return mOnItemTouchListener.onItemTouch(MyViewHolderForRecyclerView.this, v, event, getPosition());
                }
                return false;
            }
        });


        /**
        //新增的checkbox,togglebutton的点击状态改变监听器
        mConvertView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (mOnCheckedChangeListener != null) {
                    return mOnCheckedChangeListener.onCheckedChanged(MyViewHolderForRecyclerView.this, compoundButton, isChecked, getPosition());
                }
            }
        });
         */

    }
}