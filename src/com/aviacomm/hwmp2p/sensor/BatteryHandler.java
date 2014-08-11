package com.aviacomm.hwmp2p.sensor;

import com.aviacomm.hwmp2p.client.MessageEnum;
import com.aviacomm.hwmp2p.util.CommonSingleCircleThread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;

/*
 * Responsible for obtaining battery information.
 * It will be move to Battery View in the future.
 */
public class BatteryHandler {
	Handler handler;
	static int state;
	private final int RUN = 1;
	private final int STOP = 2;
	BatteryBroadcastReceiver broadcastReceiver;
	Context context;

	IntentFilter filter = new IntentFilter();

	public BatteryHandler(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		broadcastReceiver = new BatteryBroadcastReceiver();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
	}

	public void start() {
		state = RUN;
		context.registerReceiver(broadcastReceiver, filter);

	}

	public void stop() {
		state = STOP;
		context.unregisterReceiver(broadcastReceiver);
	}

	CommonSingleCircleThread towarn = new CommonSingleCircleThread() {
		int currentWarnState = 0;

		public void setUp() {
			this.setInterval(500);
		}
		public void tearDown() {
			handler.obtainMessage(MessageEnum.LOWBATTERYWARN, currentWarnState,
					1).sendToTarget();
		}
		public void oneTask() {
			currentWarnState = currentWarnState == 0 ? 1 : 0;
			handler.obtainMessage(MessageEnum.LOWBATTERYWARN, currentWarnState,
					0).sendToTarget();
		}
	};
	public final int WARNBATTERYLEVEL = 10;

	public class BatteryBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int level = rawlevel * 100 / scale;
			handler.obtainMessage(MessageEnum.BATTERYCHANGE, level, 0)
					.sendToTarget();
			if (level <= WARNBATTERYLEVEL) {
				if (!towarn.isRunning())
					towarn.start();
			} else {
				if (towarn.isRunning())
					towarn.stop();
			}
		}
	}
}
