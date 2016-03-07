package com.penjin.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.penjin.android.R;
import com.penjin.android.activity.gongzuo.PenjinBaobiaoActivity;
import com.penjin.android.widget.CircleProgressBar;

/**
 * Created by maotiancai on 2016/1/5.
 */
public class TodayKaoqinFragment extends Fragment implements View.OnClickListener {

    ImageView circlePoint;
    CircleProgressBar circleProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_fragment_kaoqin_today, container, false);
        circlePoint = (ImageView) rootView.findViewById(R.id.circlePoint);
        circleProgressBar = (CircleProgressBar) rootView.findViewById(R.id.circleProgressBar);
        circleProgressBar.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circleProgressBar:
                Intent intent = new Intent(getActivity(), PenjinBaobiaoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
