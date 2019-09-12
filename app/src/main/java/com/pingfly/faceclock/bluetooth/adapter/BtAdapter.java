package com.pingfly.faceclock.bluetooth.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ActionProvider;
import android.view.ViewGroup;

import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bluetooth.adapter.holder.BondedViewHolder;
import com.pingfly.faceclock.bluetooth.adapter.holder.DeviceViewHolder;
import com.pingfly.faceclock.bluetooth.adapter.holder.NewViewHolder;
import com.pingfly.faceclock.bluetooth.adapter.holder.NoDeviceFoundViewHolder;
import com.pingfly.faceclock.bluetooth.base.i.OnItemClickListener;
import com.pingfly.faceclock.bluetooth.bean.DataItem;
import com.pingfly.faceclock.bluetooth.base.BaseViewHolder;
import com.pingfly.faceclock.bluetooth.bean.Device;
import com.pingfly.faceclock.bluetooth.bean.NewDeviceHeader;
import com.pingfly.faceclock.bluetooth.bean.NoDeviceFoundHeader;


import java.util.ArrayList;

public class BtAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private ArrayList<DataItem> dataItems;
    private OnItemClickListener onItemClickListener;

    public BtAdapter(ArrayList<DataItem> dataItems){
        this.dataItems = dataItems;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == AppConst.DATA_TYPE_DEVICE_BONDED_HEADER){
            return new BondedViewHolder(parent);
        }else if(viewType == AppConst.DATA_TYPE_DEVICE_NEW_HEADER){
            return new NewViewHolder(parent);
        }else if(viewType == AppConst.DATA_TYPE_DEVICE_NEW || viewType == AppConst.DATA_TYPE_DEVICE_BONDED){
            return new DeviceViewHolder(parent, onItemClickListener);
        }else if(viewType == AppConst.DATA_TYPE_NO_DEVICE_FOUND){
            return new NoDeviceFoundViewHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        switch (dataItems.get(position).getType()){
            case AppConst.DATA_TYPE_DEVICE_BONDED:
            case AppConst.DATA_TYPE_DEVICE_NEW:
                ((DeviceViewHolder)holder).bindData((Device)dataItems.get(position).getData());
                break;
            case AppConst.DATA_TYPE_DEVICE_BONDED_HEADER:
                break;
            case AppConst.DATA_TYPE_DEVICE_NEW_HEADER:
                ((NewViewHolder)holder).bindData((NewDeviceHeader) dataItems.get(position).getData());
                break;
            case AppConst.DATA_TYPE_NO_DEVICE_FOUND:
                ((NoDeviceFoundViewHolder)holder).bindData((NoDeviceFoundHeader)dataItems.get(position).getData());
                break;
            default:break;
        }

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataItems.get(position).getType();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
