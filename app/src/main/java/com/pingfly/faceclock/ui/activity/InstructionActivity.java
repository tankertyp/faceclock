package com.pingfly.faceclock.ui.activity;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.pingfly.faceclock.R;
import com.pingfly.faceclock.ui.base.BaseActivity;
import com.pingfly.faceclock.ui.base.BasePresenter;
import com.pingfly.faceclock.util.LogUtils;
import com.pingfly.faceclock.util.UIUtils;

import butterknife.BindView;

public class InstructionActivity extends BaseActivity {

    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.llContent)
    LinearLayout mLlContent;


    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.instruction_title));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        mScrollView.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_MOVE:
                    // 滑动条的滚动距离<=0,说明在顶部
                    if(mScrollView.getScrollY() <= 0){
                        LogUtils.i("Instruction","top");
                    }
                    // layout的总高度 <= 滑动条的滚动距离+-屏幕的高度
                    if(mLlContent.getMeasuredHeight() <= mScrollView.getScrollY()+mScrollView.getHeight()){
                        LogUtils.i("Instruction","bottom");
                    }
                    break;
            }

            return false;
        });

    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_instruction;
    }


}
