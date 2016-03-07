package com.penjin.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.penjin.android.view.PenjinKaoqinListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maotiancai on 2016/1/6.
 */
public class KaoqinListAdapter extends BaseAdapter {

    JSONArray ja;
    Context context;

    public KaoqinListAdapter() {
    }

    public KaoqinListAdapter(JSONArray ja, Context context) {
        this.ja = ja;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ja.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return ja.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PenjinKaoqinListItem item = new PenjinKaoqinListItem(context, (JSONObject) ja.opt(position), position+1);
        item.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        return item;
    }
}
