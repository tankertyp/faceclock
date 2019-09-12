package com.pingfly.faceclock.ui.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 *  负责从model层获取数据、整理数据、行为处理等。处理后调用view显示数据。
 * @param <V>
 */
public class BasePresenter<V> {



    /*================== 以下是网络请求接口 ==================*/

    public BaseActivity mContext;

    /**
     * 在子类的构造函数中，设定参数为context，这时候可以presenter调用接口来实现对界面的操作。
     * @param context
     */
    public BasePresenter(BaseActivity context) {
        mContext = context;
    }

    protected Reference<V> mViewRef;

    /**
     * 绑定view
     * @param view
     */
    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    /**
     * 解绑View
     */

    // 试想一下，如果在点击 Button 之后，Model 获取到数据之前，退出了 Activity，此时由于 Activity 被 Presenter 引用，
    // 而 Presenter 正在进行耗时操作，会导致 Activity 的对象无法被回收，造成了内存泄漏。
    //采用 Activity 作为 V 层，则可以在 P 层添加一个销毁 V 层的方法，P 层增加了类似的生命周期的方法，用来在退出 Activity 的时候取消持有的 V 层引用。
    public void detachView() {
        if (mViewRef != null) {   //对 mViewRef 进行判空操作
            mViewRef.clear();


            // 防止内存泄漏:在 Activity 退出的时候，把 Presenter 对中 View 的引用置为空即可
            mViewRef = null;
        }
    }

    public V getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }

}
