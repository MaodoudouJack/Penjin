package com.penjin.android.utils;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

/**
 * 
 * =============================================================================
 * 
 * 描述：跟App相关的辅助类
 * 
 * 作者： 好奇的猫儿
 * 
 * 邮件： 1240306775@qq.com
 * 
 * 日期： 2015-4-9下午2:35:27
 * 
 * =============================================================================
 */
public class AppUtils {

	private AppUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取应用程序版本名称信息
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取应用程序版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * IMEI（International Mobile Equipment
	 * Identity，移动设备国际识别码，又称为国际移动设备标识）是手机的唯一识别号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String imei = telephonyManager.getDeviceId();

		return imei;
	}

	/**
	 * 为了在无线路径和整个GSM（Global System for Mobile
	 * Communications，全球移动通信系统）移动通信网上正确地识别某个移动客户，就必须给移动客户分配一个特定的识别码。
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		String imsi = telephonyManager.getSubscriberId();

		return imsi;
	}

	/*
	 * 查询制定service class name 所在的进程名字
	 * 一般用于application初始化中，判断onCreate方法执行在哪个进程里(是activity进程还是其他remote进程)
	 */
	public static String getCurrentProcessName(Context context)
	{
			int pid=android.os.Process.myPid();//当前进程的pid
			String processName = null;
			ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			List l = am.getRunningAppProcesses();
			Iterator i = l.iterator();
			PackageManager pm = context.getPackageManager();
			while (i.hasNext()) {
				//获取正在运行的app所有进程信息
				ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
				try {
					if (info.pid == pid) {//对进程id进行匹配
						CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
						// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
						// info.processName +"  Label: "+c.toString());
						// processName = c.toString();
						processName = info.processName;
						return processName;
					}
				} catch (Exception e) {
					// Log.d("Process", "Error>> :"+ e.toString());
				}
			}
			return processName;
	}
}

