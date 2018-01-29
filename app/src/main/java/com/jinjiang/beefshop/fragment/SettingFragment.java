package com.jinjiang.beefshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.SettingActivity;
import com.jinjiang.beefshop.buletouth.utils.SharedPreferencesUtils;
import com.jinjiang.beefshop.sqlite.SqliteUtil;

/**
 * 设置fragment
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView settingBuleTouchTv;
    private TextView settingSpTv;
    private TextView settingHistoryOrderTv;
    public static final String OPEN_AUTO_CLEAR= "open_auto_clear";
    private Switch autoClearSwich;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingBuleTouchTv = (TextView) getView().findViewById(R.id.setting_buletouch);
        settingSpTv = (TextView) getView().findViewById(R.id.setting_sp);
        settingHistoryOrderTv = (TextView) getView().findViewById(R.id.setting_history_order);
        autoClearSwich = (Switch) getView().findViewById(R.id.auto_clear_switch);
        settingBuleTouchTv.setOnClickListener(this);
        settingSpTv.setOnClickListener(this);
        settingHistoryOrderTv.setOnClickListener(this);
        autoClearSwich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtils.setBoolean(OPEN_AUTO_CLEAR, b);
                if(b){
                    settingSpTv.setVisibility(View.GONE);
                    settingHistoryOrderTv.setVisibility(View.GONE);
                }else {
                    settingSpTv.setVisibility(View.VISIBLE);
                    settingHistoryOrderTv.setVisibility(View.VISIBLE);
                }
            }
        });
        boolean aBoolean = SharedPreferencesUtils.getBoolean(OPEN_AUTO_CLEAR, true);
        autoClearSwich.setChecked(aBoolean);
    }

    @Override
    public void onClick(View view) {
        if(view == settingBuleTouchTv){
            Intent intent = new Intent();
            intent.setClass(getContext(), SettingActivity.class);
            startActivityForResult(intent, 11);
        }else if(view == settingSpTv){
            SharedPreferencesUtils.clearSpByName(ProductsFragment.CURRENT_ORDER_NUM);
            Toast.makeText(getContext(),"清空当前排号数成功",Toast.LENGTH_LONG).show();
        }else if(view == settingHistoryOrderTv){
            SqliteUtil.getInstance().clearTableData(getContext());
            Toast.makeText(getContext(),"清空历史订单成功",Toast.LENGTH_LONG).show();
        }
    }
}
