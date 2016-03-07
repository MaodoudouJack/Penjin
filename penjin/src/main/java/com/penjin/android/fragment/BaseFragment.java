package com.penjin.android.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment implements OnClickListener  {
	
	protected  ImageView left;
	protected  ImageView right;
	protected  TextView center;
	
	
	@Override
	public void onClick(View v) {
	}
	
	@Override
	public  View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	


	

}
