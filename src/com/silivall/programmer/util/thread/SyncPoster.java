package com.silivall.programmer.util.thread;

public class SyncPoster {

	private boolean end = false;
	private Runnable runnable;
	
	public SyncPoster(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		synchronized (this) {
			runnable.run();
			end = true;
			try {
				this.notifyAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void waitRun() {
		if (!end) {
			synchronized (this) {
				if (!end) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
