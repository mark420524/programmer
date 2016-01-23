package com.silivall.programmer.util.thread;

import java.util.LinkedList;
import java.util.Queue;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class HandlerPoster extends Handler {
	
	private static final String TAG = HandlerPoster.class.getName();

	private final int async = 0x0;
	
	private final int sync = 0x1;
	
	private final Queue<Runnable> asyncPool;
	
	private final Queue<SyncPoster> syncPool;
	
	private final int maxMillisInsideHandleMessage;  
    private boolean asyncActive;  
    private boolean syncActive;  
  
    
    public HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {  
        super(looper);  
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;  
        asyncPool = new LinkedList<Runnable>();  
        syncPool = new LinkedList<SyncPoster>();  
    }
    
    public void dispose() {
    	this.removeCallbacksAndMessages(null);
    	this.asyncPool.clear();
    	this.syncPool.clear();
    }
    
    public void async(Runnable runnable) {
    	synchronized (asyncPool) {
			asyncPool.offer(runnable);
			if (!asyncActive) {
				asyncActive = true;
				if (!sendMessage(obtainMessage(async))) {
					Log.e(TAG, "发送异步消息给handler失败");
				}
			}
			
		}
    }
    
    public void sync(SyncPoster syncPoster) {
    	synchronized (syncPool) {
    		syncPool.offer(syncPoster);
    		if (!syncActive) {
    			syncActive = true;
    			if (!sendMessage(obtainMessage(sync))) {
    				Log.e(TAG, "发送同步消息给handler失败");
    			}
    		}
		}
    }
    
    @Override
    public void handleMessage(Message msg) {
    	if (msg.what==async) {
    		boolean rescheduled = false;
    		try {
	    		long start = SystemClock.uptimeMillis();
	    		while (true) {
	    			Runnable runnable = asyncPool.poll();
	    			if (runnable==null) {
	    				synchronized (asyncPool) {
	    					runnable = asyncPool.poll();
	    					asyncActive = false;
	    					return;
						}
	    			}
	    			runnable.run();
	    			long timeInMethod = SystemClock.uptimeMillis()-start;
	    			if (timeInMethod>=maxMillisInsideHandleMessage) {
	    				if (!sendMessage(obtainMessage(async))) {
	    					Log.e(TAG, "handler处理异步消息给失败");
	    				}else{
	    					rescheduled = true;
	    					return;
	    				}
	    			}
	    		}
    		} finally {
				asyncActive = rescheduled;
			}
    	} else if(msg.what==sync) {
    		boolean rescheduled = false;  
            try {  
                long started = SystemClock.uptimeMillis();  
                while (true) {  
                    SyncPoster post = syncPool.poll();  
                    if (post == null) {  
                        synchronized (syncPool) {  
                            // Check again, this time in synchronized  
                            post = syncPool.poll();  
                            if (post == null) {  
                                syncActive = false;  
                                return;  
                            }  
                        }  
                    }  
                    post.run();  
                    long timeInMethod = SystemClock.uptimeMillis() - started;  
                    if (timeInMethod >= maxMillisInsideHandleMessage) {  
                        if (!sendMessage(obtainMessage(sync))) {  
                        	Log.e(TAG, "handler处理同步消息给失败");
                        }else{
                        	rescheduled = true;  
                        	return;  
                        }  
                    }  
                }  
            } finally {  
                syncActive = rescheduled;  
            }  
    	}else{
    		super.handleMessage(msg);
    	}
    }
}
