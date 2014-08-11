package com.aviacomm.hwmp2p.util;

/*
 * This is a loop thread for reducing code amount
 */
public class CommonSingleCircleThread implements Runnable {
	int state;
	private final int RUN = 1;
	private final int STOP = 2;
	private int interval = 1000;
	Thread thread;

	public CommonSingleCircleThread() {
	}

	public void setUp() {
		// to work
	}

	public void tearDown() {
		// towork
	}

	public void oneTask() {
		// to work
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getInterval() {
		return interval;
	}

	public boolean isRunning() {
		return state == RUN;
	}

	public void start() {
		state = RUN;
		if (thread != null && thread.isAlive()) {
			stop();
			try {
				// TOCHECK
				thread.join(2000);
				if (thread.isAlive())
					return;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		state = STOP;
	}

	@Override
	public void run() {
		setUp();
		while (state == RUN) {
			try {
				oneTask();
				if (interval != 0)
					Thread.sleep(interval);
			} catch (Exception e) {
				state = STOP;
				e.printStackTrace();
			}
		}
		tearDown();
	}

}