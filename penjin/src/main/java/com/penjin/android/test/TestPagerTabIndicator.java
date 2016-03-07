package com.penjin.android.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.penjin.android.R;
import com.penjin.android.fragment.MonthKaoqinFragment;
import com.penjin.android.fragment.PhoneKaoqinFragment;
import com.penjin.android.fragment.TodayKaoqinFragment;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

/**
 * Created by maotiancai on 2016/1/19.
 */
public class TestPagerTabIndicator extends FragmentActivity {

    FixedIndicatorView indicator;
    SViewPager viewPager;
    IndicatorViewPager indicatorViewPager;
    OnTransitionTextListener onTransitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_tab_indicator);
        initView();
    }

    private void initView() {
        indicator = (FixedIndicatorView) findViewById(R.id.indicator);
        viewPager = (SViewPager) findViewById(R.id.viewPager);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(true);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(3);
        //设置滑动块的方式
        ColorBar colorBar = new ColorBar(this, getResources().getColor(R.color.holo_blue_bright), 12);
        indicatorViewPager.setIndicatorScrollBar(colorBar);
        //设置滑动中，字体的颜色、大小和风格的变化
        onTransitionListener = new OnTransitionTextListener();
        onTransitionListener.setColor(R.color.holo_blue_bright,R.color.pj_light_black);
        indicatorViewPager.setIndicatorOnTransitionListener(onTransitionListener);
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"待审核", "审核通过", "审核拒绝"};
        private int[] tabIcons = {R.drawable.maintab_1_selector, R.drawable.maintab_2_selector, R.drawable.maintab_3_selector,
                R.drawable.maintab_4_selector};
        private LayoutInflater inflater;

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = (TextView) inflater.inflate(R.layout.layout_item_tab_textview, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabNames[position]);
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                return new TodayKaoqinFragment();
            } else if (position == 1) {
                return new PhoneKaoqinFragment();
            } else
                return new MonthKaoqinFragment();
        }
    }

}
