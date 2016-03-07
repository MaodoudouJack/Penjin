package com.penjin.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.penjin.android.R;

public class HuodongFragment extends BaseFragment{
	
	private View rootView;
	private TextView test;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView=inflater.inflate(R.layout.layout_fragment_huodong, container,false);
		test=(TextView)rootView.findViewById(R.id.test);
		test.setOnClickListener(this);
		initTitlebar();
		return rootView;
	}
	
	private void initTitlebar()
	{
		this.left=(ImageView) rootView.findViewById(R.id.titlebar_left_img);
		this.right=(ImageView) rootView.findViewById(R.id.titlebar_right_img);
		this.center=(TextView) rootView.findViewById(R.id.titlebar_center_text);
		
		this.left.setOnClickListener(this);
		this.right.setOnClickListener(this);
		this.center.setOnClickListener(this);
		
		center.setText("活 动");
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.test:
			Toast.makeText(getActivity(), test.getText(), Toast.LENGTH_SHORT).show();
		default:
			break;
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		System.out.println("活动Fragment被创建了");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		System.out.println("活动fragment resume");
	}
}
