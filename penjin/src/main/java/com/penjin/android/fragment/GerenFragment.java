package com.penjin.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.penjin.android.R;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.service.UserService;

public class GerenFragment extends BaseFragment {

    View rootView;

    //头像区域
    TextView jianchen;
    TextView quanchen;

    //内容区域
    View mingpian;
    View gerenInfo;
    View gongsiInfo;
    View banbenInfo;
    View tuijian;
    View bangzhu;
    View pinfen;
    View setting;

    PenjinUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.layout_fragment_geren, container, false);

        initView();

        return rootView;
    }

    private void initView() {
        jianchen = (TextView) rootView.findViewById(R.id.jianchen);
        quanchen = (TextView) rootView.findViewById(R.id.quanchen);

        mingpian = rootView.findViewById(R.id.mingpian);
        gerenInfo = rootView.findViewById(R.id.gerenInfo);
        gongsiInfo = rootView.findViewById(R.id.gongsiInfo);
        banbenInfo = rootView.findViewById(R.id.banbenInfo);
        tuijian = rootView.findViewById(R.id.tuijian);
        bangzhu = rootView.findViewById(R.id.bangzhu);
        pinfen = rootView.findViewById(R.id.pinfen);
        setting = rootView.findViewById(R.id.setting);

        mingpian.setOnClickListener(this);
        gerenInfo.setOnClickListener(this);
        gongsiInfo.setOnClickListener(this);
        banbenInfo.setOnClickListener(this);
        pinfen.setOnClickListener(this);
        tuijian.setOnClickListener(this);
        bangzhu.setOnClickListener(this);
        setting.setOnClickListener(this);

        UserService userService = UserService.getInstance(getActivity().getApplicationContext());
        currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUsername();
            if (username != null) {
                if (username.length() < 3) {
                    jianchen.setText(username);
                } else if (username.length() == 3) {
                    String jianStr = username.substring(1);
                    jianchen.setText(jianStr);
                } else if (username.length() > 3) {
                    String jianStr = username.substring(2);
                    jianchen.setText(jianStr);
                }
                quanchen.setText(username);
            }
        }
    }

    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
            case R.id.mingpian:
                intent = new Intent(getActivity(), com.penjin.android.activity.geren.MinpianActivity.class);
                startActivity(intent);
                break;
            case R.id.gerenInfo:
                intent = new Intent(getActivity(), com.penjin.android.activity.geren.GerenInfoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = UserService.getInstance(this.getActivity()).getCurrentUser();
        String username = currentUser.getUsername();
        if (username != null) {
            if (username.length() < 3) {
                jianchen.setText(username);
            } else if (username.length() == 3) {
                String jianStr = username.substring(1);
                jianchen.setText(jianStr);
            } else if (username.length() > 3) {
                String jianStr = username.substring(2);
                jianchen.setText(jianStr);
            }
            quanchen.setText(username);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        System.out.println("hiddent=" + hidden);
        if (hidden == false) {
            currentUser = UserService.getInstance(this.getActivity()).getCurrentUser();
            String username = currentUser.getUsername();
            if (username != null) {
                if (username.length() < 3) {
                    jianchen.setText(username);
                } else if (username.length() == 3) {
                    String jianStr = username.substring(1);
                    jianchen.setText(jianStr);
                } else if (username.length() > 3) {
                    String jianStr = username.substring(2);
                    jianchen.setText(jianStr);
                }
                quanchen.setText(username);
            }
        }
        super.onHiddenChanged(hidden);
    }
}
