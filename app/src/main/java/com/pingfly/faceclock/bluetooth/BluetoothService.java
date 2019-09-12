package com.pingfly.faceclock.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pingfly.faceclock.app.AppConst;
import com.pingfly.faceclock.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {

    // Debugging
    private static final String TAG = "BluetoothService";

    private final Handler mHandler;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothService";

    private static final UUID MY_UUID =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");


    // 显示当前连接状态
    public static final int STATE_NONE = 0;       //  we're doing nothing
    public static final int STATE_LISTEN = 1;     //  now listening for incoming connections
    public static final int STATE_CONNECTING = 2; //  now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private BluetoothAdapter bluetoothAdapter;
    //用来连接端口的线程
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private ConnectThread mConnectThread;
    private int mState;
    private int mNewState;




    public BluetoothService(Context context, Handler handler){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }

    private synchronized void updateState() {
        mState = getState();
        LogUtils.d(TAG, "updateState() " + mNewState + " -> " + mState);
        mNewState = mState;
    }

    public int getState() {
        return mState;
    }

    //public void setState(int state) {
    //    this.mState = state;
    //}


    /**
     * 开启服务监听
     */
    public synchronized void start(){

        //取消客户端的连接线程
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }


        //开启蓝牙的等待连接的服务端线程
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }

        updateState();
    }


    public synchronized void connect(BluetoothDevice device) {

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        updateState();
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        LogUtils.d(TAG, "connected: ");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(AppConst.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(AppConst.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }



    /**
     * Stop all threads
     */
    public synchronized void stop() {
        LogUtils.e(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mState = STATE_NONE;
        updateState();
        //setState(STATE_NONE);
    }





    //连接等待线程
    class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            //获取服务器监听端口
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }   catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            setName("AcceptThread");
            //监听端口
            BluetoothSocket socket = null;
            while(mState != STATE_CONNECTED) {
                try {
                    LogUtils.e(TAG, "run: AcceptThread 阻塞调用，等待连接");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "run: ActivityThread fail");
                    break;
                }
                //获取到连接Socket后则开始通信
                if(socket != null){
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //传输数据，服务器端调用
                                LogUtils.e(TAG, "run: 服务器AcceptThread传输" );
                                connected(socket, socket.getRemoteDevice());
                                write("Hello,I am the server!".getBytes());
                                break;

                            case STATE_NONE:

                            case STATE_CONNECTED:
                                // 没有准备好或者终止连接
                                try {
                                    socket.close();
                                    LogUtils.d(TAG, "Server's socket is closed");
                                } catch (IOException e) {
                                    LogUtils.e(TAG, "Could not close unwanted socket" + e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel(){
            LogUtils.e(TAG, "close: activity Thread" );
            try {
                if(mmServerSocket != null)
                    mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "close: activity Thread fail");
            }
        }
    }


    /*
     * 如果是服务器端，需要建立监听，注意监听的是某个服务的UUID，服务器监听类
     */
    class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                //建立通道
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "ConnectThread: fail" );
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        @Override
        public void run() {
            super.run();
            //建立后取消扫描
            bluetoothAdapter.cancelDiscovery();

            try {
                LogUtils.e(TAG, "run: connectThread 等待" );
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    LogUtils.e(TAG, "run: unable to close" );
                }
                //TODO 连接失败显示
                BluetoothService.this.start();
            }


            // 重置
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            //Socket已经连接上了，默认安全,客户端才会调用
            LogUtils.e(TAG, "run: connectThread 连接上了,准备传输");
            connected(mmSocket, mmDevice);
        }

        public void cancel(){
            try {
                if(mmSocket != null)
                    mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 用来传输数据的线程
     */
    class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                if(mmSocket != null){
                    //获取连接的输入输出流
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }
        @Override
        public void run() {
            LogUtils.i(TAG, "BEGIN mConnectedThread");
            super.run();
            //读取数据
            byte[] buffer = new byte[1024];
            int bytes;
            while (mState == STATE_CONNECTED){
                try {
                    bytes = mmInStream.read(buffer);
                    LogUtils.d(TAG, "Already Connected");
                    //TODO 分发到主线程显示
                    mHandler.obtainMessage(AppConst.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                    LogUtils.e(TAG, "run: read " + new String(buffer , 0 , bytes) );
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "run: Transform error"  + e.toString());
                    break;
                }
            }
        }

        /**
         * 写入数据传输
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                //TODO 到到UI显示
                mHandler.obtainMessage(AppConst.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                LogUtils.e(TAG, "Exception during write " + e);
            }
        }

        public void cancel() {
            try {
                if(mmSocket != null)
                    mmSocket.close();
            } catch (IOException e) {
                LogUtils.e(TAG, "close() of connect socket failed" + e);
            }
        }
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }


}
