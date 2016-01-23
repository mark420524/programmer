package com.silivall.programmer.util.thread;

import android.os.Looper;

/**
 * 线程请求封装类
 * @author mark
 *
 */
public class ThreadToolKit {
	
	private static HandlerPoster mainPoster = null;
	
	private static HandlerPoster getMainPoster(int maxMillisInsideHandleMessage) {
		if (mainPoster==null) {
			synchronized (ThreadToolKit.class) {
				if (mainPoster==null) {
					mainPoster = new HandlerPoster(Looper.getMainLooper(), maxMillisInsideHandleMessage);
				}
			}
		}
		return mainPoster;
	}

	/**
	 * 异步加载
	 * @param runnable
	 * @param maxMillisInsideHandleMessage
	 */
	public static void runOnMainThreadAsync(Runnable runnable,int maxMillisInsideHandleMessage) {
		if (runnable==null) {
			return;
		}
		if (Looper.myLooper()==Looper.getMainLooper()) {
			runnable.run();
			return;
		}
		getMainPoster(maxMillisInsideHandleMessage).async(runnable);
	}
	
	/**
	 * 同步加载
	 * @param runnable
	 * @param maxMillisInsideHandleMessage
	 */
	public static void runOnMainThreadSync(Runnable runnable,int maxMillisInsideHandleMessage) {
		if (runnable==null) {
			return;
		}
		if (Looper.myLooper()==Looper.getMainLooper()) {
			runnable.run();
			return;
		}
		SyncPoster poster = new SyncPoster(runnable);
		getMainPoster(maxMillisInsideHandleMessage).sync(poster);
		poster.waitRun();
	}
	
	public static void dispose() {
		if (mainPoster!=null) {
			mainPoster.dispose();
			mainPoster = null;
		}
	}
}
