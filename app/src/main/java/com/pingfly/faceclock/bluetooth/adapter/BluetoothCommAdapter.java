package com.pingfly.faceclock.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bluetooth.adapter.holder.ReceiveTxtViewHolder;
import com.pingfly.faceclock.bluetooth.adapter.holder.SendTxtViewHolder;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;
import com.pingfly.faceclock.bluetooth.bean.DataItem;

import java.util.ArrayList;

public class BluetoothCommAdapter extends RecyclerView.Adapter{

    private ArrayList<DataItem> dataItems;

    public BluetoothCommAdapter(ArrayList<DataItem> dataItems){
        this.dataItems = dataItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if( viewType == AppConst.MESSAGE_TYPE_RECEIVE_TXT){
            return new ReceiveTxtViewHolder(parent);
        }else if(viewType == AppConst.MESSAGE_TYPE_SEND_TXT){
            return new SendTxtViewHolder(parent);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(null != dataItems.get(position).getData()){
            ((BaseViewHolder)holder).bindData(dataItems.get(position).getData());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

}
