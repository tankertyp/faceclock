package com.pingfly.faceclock.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.houxya.bthelper.BtHelper;
import com.houxya.bthelper.bean.MessageItem;
import com.houxya.bthelper.i.OnSendMessageListener;
import com.houxya.bthelper.receiver.BroadcastType;
import com.houxya.bthelper.receiver.BtAcceptReceiver;
import com.houxya.bthelper.receiver.BtConnectionLostReceiver;
import com.houxya.bthelper.receiver.BtStateReceiver;
import com.pingfly.faceclock.R;
import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.bluetooth.BluetoothService;
import com.pingfly.faceclock.bluetooth.adapter.BtAdapter;
import com.pingfly.faceclock.bluetooth.bean.DataItem;
import com.pingfly.faceclock.bluetooth.bean.Device;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

import static com.pingfly.faceclock.bluetooth.BluetoothService.STATE_CONNECTED;


public class MusicModeActivity extends BaseActivity {

    private static final String TAG = "MusicModeActivity";
    private static final int REQUEST_CODE_PERMISSION = 101;
    public static final String DEVICE_NAME_INTENT = "device_name";
    private static final int UPDATE_DATA = 0x666;
    private BluetoothService mBluetoothService;

    private Boolean isTraceChecked = false;    // 跟随按钮状态，默认未选中
    public static final long MS_IN_FUTURE = DateUtils.MINUTE_IN_MILLIS / 15;    // 总共的时间是4s.
    public static final long MS_IN_FUTURE2 = DateUtils.MINUTE_IN_MILLIS / 60;    // 总共的时间是1s.
    public static final int MAX_PROGRESS = 100;
    public static final String PROGRESS_PROPERTY = "progress";
    private ObjectAnimator objectAnimator;

    private ArrayList<DataItem> dataItems;
    private BtHelper mBtHelper;
    private BtAdapter btAdapter;
    private BtStateReceiver mBtStateReceiver;
    private BtConnectionLostReceiver mBtConnectionLostReceiver;
    private BtAcceptReceiver mBtAcceptReceiver;
    private int bondedDeviceNum = 0;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tbTrace)
    ToggleButton mTbTrace;


    @Override
    public void initView() {
        setToolbarTitle(getString(R.string.music_mode));
    }

    @Override
    public void initReceiver() {

        //蓝牙是异步操作,只有蓝牙完全开启的时候，我们才能够获取到已绑定的蓝牙设备的信息
        mBtStateReceiver = new BtStateReceiver() {

            @Override
            public void OnBtStateOFF() {
                //invisibleSendAndChangeItemState();
                //Toast.makeText(MusicModeActivity.this, "你已经进入了没有蓝牙的异次元...aaa", Toast.LENGTH_SHORT).show();
                UIUtils.showToast(UIUtils.getString(R.string.disconnect_bluetooth));
            }

            @Override
            public void OnBtStateON() {
                if (null != mBtHelper.getBondedDevices() && dataItems.size() == 1) {

                    int j = 0;
                    for (BluetoothDevice bluetoothDevice : mBtHelper.getBondedDevices()) {
                        j++;
                        DataItem<Device> dataItem = new DataItem<Device>();
                        dataItem.setType(AppConst.DATA_TYPE_DEVICE_BONDED);
                        dataItem.setData(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                        dataItems.add(dataItem);
                    }
                    btAdapter.notifyItemRangeInserted(1, j);
                }
                bondedDeviceNum = Objects.requireNonNull(mBtHelper.getBondedDevices()).size();

                LogUtils.d("TAG", ">>>>>>>>>>>>>>>>>OnBtStateON>>>>>>>>>>>>");
            }
        };
        registerReceiver(mBtStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        mBtConnectionLostReceiver = new BtConnectionLostReceiver() {
            @Override
            public void OnConnectionLost() {
                //invisibleSendAndChangeItemState();
            }
        };
        registerReceiver(mBtConnectionLostReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_CONNECTION_LOST));

        /**
         mBtAcceptReceiver = new BtAcceptReceiver() {

        @Override public void OnAccept(final BluetoothDevice device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(device.getName() + "请求和你通信");
        builder.setPositiveButton("开始聊天", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        Intent intent = ChatActivity.getIntentStartActivity(MainActivity.this, device.getName());
        startActivity(intent);
        dialog.dismiss();
        }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        }
        });
        builder.create().show();
        }
        };
         registerReceiver(mBtAcceptReceiver, new IntentFilter(BroadcastType.BROADCAST_TYPE_ACCEPT_CONNECTION));
         */
    }


    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        /*使用lambda表达式写法**/
        mTbTrace.setOnCheckedChangeListener((buttonView, isChecked) -> {

            LogUtils.d(TAG, "onCheckedChanged: " + isChecked);
            if (isChecked) {
                isTraceChecked = true;
                initProgressBar();
                UIUtils.showToast(UIUtils.getString(R.string.trace_start));

                String trace_on = "200";
                //sendMessage(s);
                BtHelper.getDefault().sendMessage(new MessageItem(trace_on), new OnSendMessageListener() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onConnectionLost() {
                        //在这里监听的连接中断的话要尝试发送一次消息才能监听到
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                if (objectAnimator != null && objectAnimator.isRunning()) {
                    objectAnimator.end();
                }
            } else {
                isTraceChecked = false;
                initProgressBar2();
                UIUtils.showToast(UIUtils.getString(R.string.trace_stop));
                String trace_off = "201";
                //sendMessage(s);
                BtHelper.getDefault().sendMessage(new MessageItem(trace_off), new OnSendMessageListener() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onConnectionLost() {
                        //在这里监听的连接中断的话要尝试发送一次消息才能监听到
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

            }
            objectAnimator.start();

        });
    }

    /**
     * 如果用户从pause的状态又回到了你的activity，这个系统resume这个activity并且调用了onResume()这个方法
     */
    @Override
    protected void onResume() {
        super.onResume();
        //initProgressBar();
        LogUtils.e(TAG, "onResume: ");
    }

    private void initProgressBar() {
        // 当前进度
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setProgress(0);

        objectAnimator = ObjectAnimator
                .ofInt(progressBar, PROGRESS_PROPERTY, progressBar.getMax())
                .setDuration(MS_IN_FUTURE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();

    }

    private void initProgressBar2() {
        // 当前进度
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setProgress(100);

        objectAnimator = ObjectAnimator
                .ofInt(progressBar, PROGRESS_PROPERTY, 0)
                .setDuration(MS_IN_FUTURE2);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();

    }


    private void sendMessage(String message) {

    }

    /**
     * onPause()这个方法让你能够结束一些正在进行的任务，而这些任务在停止的时候就不能继续了
     * 当你的activity被pause的时候，这个activity实例在内存中是占用位置的，而且在Activity被resume的时候会被再次唤醒
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (objectAnimator != null && objectAnimator.isRunning()) {
            objectAnimator.end();
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_music_mode;
    }

}
