package com.penjin.android.activity.apply;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.fragment.ApplyDoingFragment;
import com.penjin.android.fragment.ApplyDoneFragment;
import com.penjin.android.fragment.ApplyRejectFragment;
import com.penjin.android.fragment.MonthKaoqinFragment;
import com.penjin.android.fragment.PhoneKaoqinFragment;
import com.penjin.android.fragment.TodayKaoqinFragment;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.view.CustomProgressDialog;
import com.penjin.android.view.TitleBarView;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 查看我的申请（包含“申请中”、"已通过"和"已拒绝"
 * Created by maotiancai on 2016/1/14.
 */
public class MyApplyDetailActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * 滑动指示器部分
     */
    FixedIndicatorView indicator;
    SViewPager viewPager;
    IndicatorViewPager indicatorViewPager;
    OnTransitionTextListener onTransitionListener;

    private TitleBarView titleBarView;
    private ListView listView;

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"待审核", "审核通过", "审核拒绝"};
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
                return new ApplyDoingFragment();
            } else if (position == 1) {
                return new ApplyDoneFragment();
            } else
                return new ApplyRejectFragment();
        }
    }

    private void initIndicator() {
        indicator = (FixedIndicatorView) findViewById(R.id.indicator);
        viewPager = (SViewPager) findViewById(R.id.viewPager);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setCanScroll(true);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(3);
        //设置滑动块的方式
        ColorBar colorBar = new ColorBar(this, getResources().getColor(R.color.holo_blue_bright), 12);
        indicatorViewPager.setIndicatorScrollBar(colorBar);
        onTransitionListener = new OnTransitionTextListener();
        onTransitionListener.setColor(R.color.holo_blue_bright, R.color.pj_light_black);
        indicatorViewPager.setIndicatorOnTransitionListener(onTransitionListener);
        int type = getIntent().getExtras().getInt("type");
        if (type == 1)
            viewPager.setCurrentItem(1);
        else if (type == 0)
            viewPager.setCurrentItem(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_my_apply);
        initView();
        initTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        initIndicator();
    }

    private void initTitleBar() {
        titleBarView = (TitleBarView) findViewById(R.id.titleBar);
        titleBarView.setTitleBarListener(new TitleBarView.TitleBarListener() {
            @Override
            public void left(View view) {
                finish();
            }

            @Override
            public void center(View view) {

            }

            @Override
            public void right(View view) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
