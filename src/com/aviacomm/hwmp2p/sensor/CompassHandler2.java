package com.aviacomm.hwmp2p.sensor;

import com.aviacomm.hwmp2p.HWMP2PClient;
import com.aviacomm.hwmp2p.MLog;
import com.aviacomm.hwmp2p.MessageEnum;
import com.aviacomm.hwmp2p.sensor.BatteryHandler.BatteryBroadcastReceiver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;

/*
 * IT WILL BE REPLEACED BY CompassHandler IN THE FUTURE!
 */
public class CompassHandler2 implements SensorEventListener {
	static int state;
	private final int RUN = 1;
	private final int STOP = 2;

	Handler handler;
	Context context;
	// 管理传感器对象
	private android.hardware.SensorManager mSensorManager;
	// 传感器对象
	private Sensor mOrientationSensor;

	// 自定义的view
	// private SampleView mView;

	public CompassHandler2(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		mSensorManager = (android.hardware.SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);

	}

	public void start() {
		mSensorManager.registerListener(this, mOrientationSensor,
				android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void stop() {
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	public static int tolog_count = 0;

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mOrientationSensor) {
			if (tolog_count++ % 20 == 0) {
				// HWMP2PClient.log.i(String.format("Orientation: %f",
				// event.values[0]));
			}
			handler.obtainMessage(MessageEnum.ORIENTATIONCHANGE,
					Float.valueOf(event.values[0])).sendToTarget();
		}
	}
}