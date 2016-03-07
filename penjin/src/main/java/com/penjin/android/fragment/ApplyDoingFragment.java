package com.penjin.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.constants.HttpConstants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 正在审核的申请列表Fragment
 * Created by maotiancai on 2016/1/20.
 */
public class ApplyDoingFragment extends ApplyListFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_apply_doing, container, false);
    }

    /**
     * 复写网络请求过程
     */
    @Override
    protected void launchRequest() {
        if (NetUtils.hasNetwork(getActivity())) {
            RequestParams requestParams = new RequestParams();
            requestParams.put("staffNumber", user.getStaffNum());
            requestParams.put("companyId", user.getCompanyId());
            requestParams.put("sn", httpService.getSn());
            requestParams.put("applicationType", 1);
            try {
                httpService.postRequestAsync(HttpConstants.HOST + HttpConstants.ApplicationDetail, requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        mHandler.sendEmptyMessage(FETCH_LIST_ERROR);
                        if (s != null) {
                            System.out.println(s);
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        System.out.println(s);
                        try {
                            JSONObject jo = new JSONObject(s);
                            if (jo.getBoolean("result")) {
                                ApplyDoingFragment.this.listData = jo.getJSONArray("data");
                                mHandler.sendEmptyMessage(FETCH_LIST_OK);
                            } else {
                                mHandler.sendEmptyMessage(FETCH_LIST_ERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(FETCH_LIST_ERROR);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(FETCH_LIST_ERROR);
            }
        } else {

        }
    }
}
