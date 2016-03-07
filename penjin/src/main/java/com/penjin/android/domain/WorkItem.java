package com.penjin.android.domain;

public class WorkItem {
	
	private String workName;
	private int imageId;
	private Class activityClass;
	private int workType;
	
	public WorkItem(String name,int imageId)
	{
		this.workName=name;
		this.imageId=imageId;
	}
	
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	
	
}
