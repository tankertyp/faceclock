package com.pingfly.faceclock.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface OnItemLongClickListener {
    /**
     * @param helper
     * @param parent   如果是RecyclerView的话，parent为空
     * @param itemView
     * @param position
     */
    boolean onItemLongClick(MyViewHolder helper, ViewGroup parent, View itemView, int position);
}
