package com.pingfly.faceclock.alarmclock.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pingfly.faceclock.R;

import java.util.Objects;

public class AddItemView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private Context mContext;
    private TextView tv_title;
    private TextView tv_desc;
    private ImageView iv_menu;
    private String Title;
    private String Desc;
    private int src;

    public AddItemView(Context context) {
        //super(context);
        this(context,null);
        mContext=context;
        initView();
    }
    public AddItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //this(context, attrs);
        mContext=context;
        Title=Objects.requireNonNull(attrs).getAttributeValue(NAMESPACE,"aivtitle");
        Desc=attrs.getAttributeValue(NAMESPACE, "aivdesc");
        src=attrs.getAttributeResourceValue(NAMESPACE,"aivsrc",R.mipmap.icon_trangel);
        initView();
    }

    private void initView() {
        View.inflate(mContext, R.layout.view_add_item, this);
        tv_title = (TextView) findViewById(R.id.tv_tag);
        tv_desc = (TextView) findViewById(R.id.tv_desc_aiv);
        iv_menu = (ImageView) findViewById(R.id.iv_menu_aiv);
        setTitle(Title);
        setDesc(Desc);
        setSrc(src);
    }

    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setSrc(int resID){
        iv_menu.setImageResource(resID);
    }

}
