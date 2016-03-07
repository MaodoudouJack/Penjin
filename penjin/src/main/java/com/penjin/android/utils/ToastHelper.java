package com.penjin.android.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
	
	public static void showMessage(String msg,Context context)
	{
		Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
	}
}
