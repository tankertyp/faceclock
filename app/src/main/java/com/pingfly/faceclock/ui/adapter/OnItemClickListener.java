package com.pingfly.faceclock.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface OnItemClickListener {
    /**
     * @param helper
     * @param parent   如果是RecyclerView的话，parent为空
     * @param itemView
     * @param position
     */
    void onItemClick(MyViewHolder helper, ViewGroup parent, View itemView, int position);
}
