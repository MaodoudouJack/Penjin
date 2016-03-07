package com.penjin.android.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.penjin.android.R;
import com.penjin.android.activity.MainActivity;
import com.penjin.android.activity.gongzuo.PenjinApplyActivity;
import com.penjin.android.activity.kaoqin.GerenKaoqinActivity;
import com.penjin.android.activity.kaoqin.GerenPaibanActivity;
import com.penjin.android.activity.kaoqin.MonthKaoqinActivity;
import com.penjin.android.widget.AutoScrollViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class KaoqinFragment extends Fragment implements View.OnClickListener {

    AutoScrollViewPager kaoqinBanner;
    CirclePageIndicator kaoqinIndicator;
    List<android.support.v4.app.Fragment> fragmentList = new ArrayList<android.support.v4.app.Fragment>();

    View gerenKaoqin;
    View yueKaoqin;
    View kaoqinShengqing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_fragment_kaoqin, container, false);
        initView(rootView);
        initBanner();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //initBanner();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            // initBanner();
        }
    }

    private void initView(View rootView) {
        kaoqinBanner = (AutoScrollViewPager) rootView.findViewById(R.id.kaoqinBanner);
        kaoqinIndicator = (CirclePageIndicator) rootView.findViewById(R.id.kaoqinIndicator);

        gerenKaoqin = rootView.findViewById(R.id.gerenKaoqin);
        yueKaoqin = rootView.findViewById(R.id.yueKaoqin);
        kaoqinShengqing = rootView.findViewById(R.id.kaoqinShengqing);

        gerenKaoqin.setOnClickListener(this);
        yueKaoqin.setOnClickListener(this);
        kaoqinShengqing.setOnClickListener(this);
    }

    private void initBanner() {
        if (this.fragmentList == null || fragmentList.size() == 0) {
            System.out.println("重新创建banner了!!!!!");
            fragmentList = new ArrayList<>();
            fragmentList.add(new TodayKaoqinFragment());
            fragmentList.add(new MonthKaoqinFragment());
            fragmentList.add(new PhoneKaoqinFragment());
            FragmentManager fm = ((MainActivity) getActivity()).getSupportFragmentManager();
            FragmentPagerAdapter adapter = new FragmentPagerAdapter(fm) {
                @Override
                public android.support.v4.app.Fragment getItem(int position) {
                    return fragmentList.get(position);
                }

                @Override
                public int getCount() {
                    return fragmentList.size();
                }
            };
            kaoqinBanner.setAdapter(adapter);
            kaoqinBanner.setCurrentItem(0);
            kaoqinIndicator.setViewPager(kaoqinBanner);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.gerenKaoqin:
                intent = new Intent(getActivity(), GerenKaoqinActivity.class);
                startActivity(intent);
                break;
            case R.id.yueKaoqin:
                intent = new Intent(getActivity(), GerenPaibanActivity.class);
                startActivity(intent);
                break;
            case R.id.kaoqinShengqing:
                intent = new Intent(getActivity(), PenjinApplyActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
