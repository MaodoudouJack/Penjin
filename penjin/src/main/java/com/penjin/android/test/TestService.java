package com.penjin.android.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * 测试服务：远程启动的service会使application启动多次
 */
public class TestService extends Service {

	private static String NAME = "TEST_SERVICE";
	
	private boolean SHUTDOWN=false;
	
	Thread thread = new Thread() {

		@Override
		public void run() {
			while (!SHUTDOWN) {
				try {
					System.out.println(NAME + "：service running...");
					Thread.sleep(3000);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			System.out.println("子线程退出...");
		}

	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		//如果服务被系统意外杀死，需要关闭一些资源
		SHUTDOWN=true;
		try {
			System.out.println("等待子线程退出...");
			thread.join();
			System.out.println("等待结束，成功退出...");
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	
	
}
