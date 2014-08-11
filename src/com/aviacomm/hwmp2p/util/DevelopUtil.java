package com.aviacomm.hwmp2p.util;

import android.os.SystemClock;


public class DevelopUtil {
	public static boolean waitAndCheckUntil(waitCondition condition,
			long maxtime, long steptime) {
		long waittime = 0;
		while (!condition.task() && (waittime += steptime) < maxtime) {
			SystemClock.sleep(steptime);
		}
		return waittime<=maxtime;
	}

	public static boolean waitAndCheckUntil(waitCondition condition) {
		return waitAndCheckUntil(condition, 10000, 200);
	}

	public interface waitCondition {
		public boolean task();
	}
}
