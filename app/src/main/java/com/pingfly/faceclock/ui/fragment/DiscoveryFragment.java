package com.pingfly.faceclock.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pingfly.faceclock.R;
import com.lqr.optionitemview.OptionItemView;
import com.pingfly.faceclock.alarmclock.dao.CityDao;
import com.pingfly.faceclock.alarmclock.util.ConsUtils;
import com.pingfly.faceclock.alarmclock.util.PrefUtils;
import com.pingfly.faceclock.ui.activity.DeviceStatusActivity;
import com.pingfly.faceclock.ui.activity.MainActivity;
import com.pingfly.faceclock.ui.activity.ScanActivity;
import com.pingfly.faceclock.ui.base.BaseFragment;
import com.pingfly.faceclock.ui.presenter.DiscoveryFgPresenter;
import com.pingfly.faceclock.ui.view.IDiscoveryFgView;
import com.pingfly.faceclock.util.LogUtils;


import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

public class DiscoveryFragment extends BaseFragment<IDiscoveryFgView, DiscoveryFgPresenter>
        implements IDiscoveryFgView {

    @BindView(R.id.oivCpu)
    OptionItemView mOivCpuInfo;
    @BindView(R.id.oivMem)
    OptionItemView mOivMemInfo;
    @BindView(R.id.oivTemp)
    OptionItemView mOivTempInfo;
    @BindView(R.id.oivScan)
    OptionItemView mOivScan;


    @BindView(R.id.ll_change_city)
    LinearLayout mChangeCity;//更换城市
    @BindView(R.id.tv_current_city)
    TextView mCurrentCity;//当前城市显示

    private ArrayList<String> cityList;//城市列表数据
    private CityDao dao;//读取数据库工具
    private AlertDialog.Builder builder;//更换城市对话框


    @Override
    public void initData() {

        mCurrentCity.setText(PrefUtils.getString(((MainActivity) Objects.requireNonNull(getActivity())), ConsUtils.CURRENT_CITY, "重庆"));

    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initListener() {
        mOivCpuInfo.setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(DeviceStatusActivity.class));
        mOivMemInfo.setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(DeviceStatusActivity.class));
        mOivTempInfo.setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(DeviceStatusActivity.class));
        //mOivCpuInfo.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(ScanActivity.class));
        mOivScan.setOnClickListener(v -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(ScanActivity.class) );

        mChangeCity.setOnClickListener(v -> {
            //stopAlarmMusic();
            cityList = new ArrayList<String>();
            final View autoLayout = View.inflate(getActivity(), R.layout.auto_edit_view, null);
            final EditText autoText = (EditText) autoLayout.findViewById(R.id.et_change_city);
            ListView listHint = (ListView) autoLayout.findViewById(R.id.lv_change_city);
            initAutoEdit(autoText, listHint);
            dao = new CityDao();
            builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setTitle("输入您要更换的城市");
            builder.setView(autoLayout);
            builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String cityName = autoText.getText().toString();
                    if (cityName == null || cityName.equals("")) {
                        Toast.makeText(getActivity(), "城市不能为空", Toast.LENGTH_SHORT).show();
                    } else if (cityName.matches("^[\u4e00-\u9fa5]+$")) {
                        //cityname为汉字
                        //1.记录下来
                        PrefUtils.putString(getActivity(), ConsUtils.CURRENT_CITY, autoText.getText().toString());
                        //2.更改文字
                        changeCurrentCity(autoText.getText().toString());
                        //3.重新请求网络数据
                        PrefUtils.putlong(getActivity(), ConsUtils.Last_REQUEST_TIME, 0l);
                    } else {
                        Toast.makeText(getActivity(), "城市名只能为汉字", Toast.LENGTH_SHORT).show();
                        autoText.setText("");
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
    }


    //初始化自动补全的文本框
    private void initAutoEdit(final EditText autoText, final ListView listHint) {
        //初始化listView
        LogUtils.d("changecity", "初始化adapter");
        final myAdapter adapter=new myAdapter();
        listHint.setAdapter(adapter);
        autoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    LogUtils.d("changecity", "显示listview");
                    //如果字符大于0，显示listview
                    listHint.setVisibility(View.VISIBLE);
                    cityList = dao.find(s.toString());
                    adapter.notifyDataSetChanged();
                } else {
                    listHint.setVisibility(View.GONE);
                }
                int itemCount = listHint.getAdapter().getCount();
                LogUtils.d("changecity", "count" + itemCount);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listHint.getLayoutParams();
                if (itemCount > 4) {
                    params.height = 500;
                } else {
                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                listHint.setLayoutParams(params);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int height = listHint.getHeight();
                LogUtils.d("changecity", height + "文字改变后");

            }
        });

        listHint.setOnItemClickListener((parent, view, position, id) -> {
            TextView cityName= (TextView) view.findViewById(R.id.tv_hint_city);
            autoText.setText(cityName.getText());
            listHint.setVisibility(View.GONE);
        });
    }

    class myAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView=View.inflate(getActivity(),R.layout.item_change_city,null);
                holder=new ViewHolder();
                holder.hint= (TextView) convertView.findViewById(R.id.tv_hint_city);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.hint.setText(cityList.get(position));
            return convertView;
        }
    }
    static class ViewHolder{
        TextView hint;
    }

    //改变当前的城市文字
    private void changeCurrentCity(String city) {
        mCurrentCity.setText(city);
    }


    @Override
    protected DiscoveryFgPresenter createPresenter() {
        return new DiscoveryFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_discovery;
    }

}

