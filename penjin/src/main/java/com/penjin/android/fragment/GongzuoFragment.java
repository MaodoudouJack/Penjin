package com.penjin.android.fragment;

import com.penjin.android.R;
import com.penjin.android.utils.BitmapUtils;
import com.penjin.android.widget.AutoScrollViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GongzuoFragment extends BaseFragment {
	/**
	 * 广告条
	 */
	AutoScrollViewPager banner;
	List<View> bannerImages=new ArrayList<>();
	CirclePageIndicator indicator;

	/**
	 * 工作按钮区
	 */
	ImageView gonggao;
	ImageView rizhi;
	ImageView wendang;
	ImageView shengpi;
	ImageView qiandao;
	ImageView youjian;
	ImageView rili;
	ImageView xinzi;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view= inflater.inflate(R.layout.layout_fragment_gongzuo, null,false);
		banner=(AutoScrollViewPager)view.findViewById(R.id.banner);
		for(int i=0;i<4;i++){
			View image=LayoutInflater.from(getActivity()).inflate(R.layout.item_banner_image,null);
			if(i==1)
			{
				((ImageView)image.findViewById(R.id.banner_image)).setImageBitmap(BitmapUtils.readBitMap(getActivity(),R.drawable.banner_image_1));
			}
			bannerImages.add(image);
		}
		BannerAdapter adapter=new BannerAdapter();
		banner.setAdapter(adapter);
		banner.setCurrentItem(0);
		banner.setInterval(3000);
		indicator= (CirclePageIndicator) view.findViewById(R.id.indicator);
		indicator.setViewPager(banner);

		gonggao=(ImageView)view.findViewById(R.id.gonggao);
		rizhi=(ImageView)view.findViewById(R.id.rizhi);
		wendang=(ImageView)view.findViewById(R.id.wendang);
		shengpi=(ImageView)view.findViewById(R.id.shengpi);
		qiandao=(ImageView)view.findViewById(R.id.qiandao);
		youjian=(ImageView)view.findViewById(R.id.youjian);
		rili=(ImageView)view.findViewById(R.id.rili);
		xinzi=(ImageView)view.findViewById(R.id.xinzi);

		gonggao.setOnClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		banner.startAutoScroll();
		gonggao.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.popup_show_right_in));
		rizhi.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		wendang.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		shengpi.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		qiandao.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		youjian.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		rili.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		xinzi.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.popup_show_right_in));
		super.onResume();
	}

	@Override
	public void onPause() {
		banner.stopAutoScroll();
		super.onPause();
	}

	private class  BannerAdapter extends PagerAdapter
	{

		@Override
		public int getCount() {
			return bannerImages.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager)container).removeView(bannerImages.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager)container).addView(bannerImages.get(position));
			return bannerImages.get(position);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case  R.id.gonggao:
				v.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.scale_big));
				break;
			default:
				break;
		}
		super.onClick(v);
	}
}
