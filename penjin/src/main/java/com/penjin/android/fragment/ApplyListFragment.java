package com.penjin.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.util.NetUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.penjin.android.R;
import com.penjin.android.activity.apply.ChuchaiApplyDetailActivity;
import com.penjin.android.constants.HttpConstants;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.http.HttpService;
import com.penjin.android.service.UserService;
import com.penjin.android.view.penjin.ApplyListItem;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * 申请单的基类
 * Created by maotiancai on 2016/1/20.
 */
public class ApplyListFragment extends Fragment implements WaveSwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    public static final int FETCH_LIST_OK = 1;//加载数据成功
    public static final int FETCH_LIST_ERROR = 2;//加载数据失败
    public static final int FETCH_NO_DATA = 3;//无数据

    protected PenjinUser user;
    protected PenjinCompany company;
    protected HttpService httpService;
    protected boolean isLoaded = false;

    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    ListView mListView;
    ApplyListAdapter adapter;
    Handler mHandler;
    JSONArray listData;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHttpModule();
        initData();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoaded) {
            launchRequest();
        }
    }

    public void initData() {
        UserService userService = UserService.getInstance(getActivity().getApplicationContext());
        user = userService.getCurrentUser();
        company = userService.getCurrentCompany();
    }

    public void initView() {
        initList();
    }

    private void initList() {
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) getView().findViewById(R.id.main_swipe);
        mListView = (ListView) getView().findViewById(R.id.main_list);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        adapter = new ApplyListAdapter(getActivity());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    public void initHttpModule() {
        httpService = HttpService.getInstance(getActivity().getApplicationContext());
        mHandler = new Handler(getActivity().getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                if (msg.what == FETCH_LIST_OK) {
                    if (mWaveSwipeRefreshLayout.isRefreshing())
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                    refreshList();
                    isLoaded = true;
                } else if (msg.what == FETCH_LIST_ERROR) {
                    if (mWaveSwipeRefreshLayout.isRefreshing())
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                } else if (msg.what == FETCH_NO_DATA) {
                    if (mWaveSwipeRefreshLayout.isRefreshing())
                        mWaveSwipeRefreshLayout.setRefreshing(false);
                }
            }
        };
    }

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
                                mHandler.sendEmptyMessage(FETCH_LIST_OK);
                            } else {
                                mHandler.sendEmptyMessage(FETCH_LIST_ERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }

    }

    protected void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        launchRequest();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //判断类型
        ApplyHolder holder = (ApplyHolder) view.getTag();
        if (holder != null) {
            System.out.println("billSort" + holder.billSort);
            if (holder.billSort.startsWith(HttpConstants.chuchaiBillSort + "")) {//出差类型详情
                Intent intent = new Intent(getActivity(), ChuchaiApplyDetailActivity.class);
                intent.putExtra("BillSort", holder.billSort);
                intent.putExtra("Date", holder.billDate);
                startActivity(intent);
            }

        }

    }

    public class ApplyListAdapter extends BaseAdapter {

        private Context mContext;

        public ApplyListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            if (listData == null || listData.length() == 0)
                return 0;
            else
                return listData.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ApplyListItem item = new ApplyListItem(mContext);
            item.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            try {
                JSONObject jo = listData.getJSONObject(position);
                item.setTypeName(jo.getString("Application"));
                item.setTypeTime(jo.getString("Time"));
                item.setTypeDate(jo.getString("Date"));
                final ApplyHolder holder = new ApplyHolder();
                holder.billDate = jo.getString("Date");
                holder.billSort = jo.getString("BillSort");
                item.setTag(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return item;
        }
    }

    public class ApplyHolder {
        public String billSort;
        public String billDate;
    }
}
