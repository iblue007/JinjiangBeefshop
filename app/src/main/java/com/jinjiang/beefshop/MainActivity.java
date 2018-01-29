package com.jinjiang.beefshop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jinjiang.beefshop.buletouth.utils.SharedPreferencesUtils;
import com.jinjiang.beefshop.fragment.HistoryFragment;
import com.jinjiang.beefshop.fragment.ProductsFragment;
import com.jinjiang.beefshop.fragment.SettingFragment;
import com.jinjiang.beefshop.sqlite.SqliteUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RadioGroup discount_layout;
    /**
     * 团优惠
     */
    private RadioButton group_rb;
    /**
     * 优惠活动
     */
    private RadioButton preferential_rb;
    /**
     * 优惠券
     */
    private RadioButton coupon_rb;
    /**
     * 下划线标记
     */
    private View group_line, preferential_line, coupon_line;
    /**
     * 服务产品
     */
    private ProductsFragment productsFragment;
    private HistoryFragment historyFragment;
    private SettingFragment settingFragment;

    private ViewPager pager;

    //上门服务Fragment
    private ArrayList<Fragment> fragments;
    private int CurrentSelectPos = 0;
    private boolean isLoadHistory = false;
    /**
     * 标题名集合
     */
    private RadioButton[] titleText = null;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        discount_layout = (RadioGroup) findViewById(R.id.discount_layout);
        group_rb = (RadioButton) findViewById(R.id.group_rb);
        preferential_rb = (RadioButton) findViewById(R.id.preferential_rb);
        coupon_rb = (RadioButton) findViewById(R.id.coupon_rb);
        group_line = findViewById(R.id.group_line);
        preferential_line = findViewById(R.id.preferential_line);
        coupon_line = findViewById(R.id.coupon_line);

        titleText = new RadioButton[]{group_rb, preferential_rb, coupon_rb};
        discount_layout.setOnCheckedChangeListener(listener);

        fragments = new ArrayList<Fragment>();
        productsFragment = new ProductsFragment();
        historyFragment = new HistoryFragment();
        settingFragment = new SettingFragment();
        fragments.add(productsFragment);
        fragments.add(historyFragment);
        fragments.add(settingFragment);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(fragmentPagerAdapter);
        fragmentPagerAdapter.setFragments(fragments);
        pager.setOnPageChangeListener(new MyOnPageChangeListener());
        // 第一次启动时选中第0个tab
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String currentTime = simpleDateFormat.format(new Date());
        String current_time = SharedPreferencesUtils.getString(ProductsFragment.CURRENT_TIME, null);
        if(TextUtils.isEmpty(current_time)){
            SharedPreferencesUtils.setString(ProductsFragment.CURRENT_TIME,currentTime);
           // Log.e("====","====currentTime1:"+currentTime);
        }else {
            if(!currentTime.equals(current_time)){//不相等就清空sp的这个key
                boolean aBoolean = SharedPreferencesUtils.getBoolean(SettingFragment.OPEN_AUTO_CLEAR, true);
                if(aBoolean){
                    SharedPreferencesUtils.clearSpByName(ProductsFragment.CURRENT_TIME);
                    SharedPreferencesUtils.clearSpByName(ProductsFragment.CURRENT_ORDER_NUM);
                    SqliteUtil.getInstance().clearTableData(this);
                }
               // Log.e("====","====currentTime2:"+currentTime);
            }
        }
    }

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
                case R.id.group_rb:
                    pager.setCurrentItem(0);
                    break;
                case R.id.preferential_rb:
                    pager.setCurrentItem(1);
                    break;
                case R.id.coupon_rb:
                    pager.setCurrentItem(2);
                    break;
            }
        }
    };

    /**
     * 切换更换下划线状态
     *
     * @param position
     */
    private void setVisible(int position) {
        CurrentSelectPos = position;
        switch (position) {
            case 0:
                group_line.setVisibility(View.VISIBLE);
                preferential_line.setVisibility(View.INVISIBLE);
                coupon_line.setVisibility(View.INVISIBLE);
                break;
            case 1:
                group_line.setVisibility(View.INVISIBLE);
                preferential_line.setVisibility(View.VISIBLE);
                coupon_line.setVisibility(View.INVISIBLE);
                break;
            case 2:
                group_line.setVisibility(View.INVISIBLE);
                preferential_line.setVisibility(View.INVISIBLE);
                coupon_line.setVisibility(View.VISIBLE);
                break;
        }
        if((fragments.get(position) instanceof HistoryFragment) && !isLoadHistory){
            ((HistoryFragment)fragments.get(position)).init();
            isLoadHistory = true;
        }
    }

    /**
     * 设置选中图标的文字颜色与
     * 下划线可见
     *
     * @param index
     */
    private void chingeIndexView(int index) {
        for (int i = 0; i < titleText.length; i++) {
            titleText[i].setChecked(false);
        }
        if (index < titleText.length) {
            titleText[index].setChecked(true);
        }

    }


    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        public void setFragments(ArrayList<Fragment> fragments) {
            if (this.fragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.fragments = fragments;
            notifyDataSetChanged();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            return obj;
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            chingeIndexView(position);
            setVisible(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Fragment fragment = fragments.get(CurrentSelectPos);
                if(fragment instanceof  ProductsFragment){
                    if(((ProductsFragment) fragment).isCarShopVisible()){
                        return  false;
                    }
                }
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 800) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
