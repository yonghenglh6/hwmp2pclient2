package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.client.ClientConfig;
import com.aviacomm.hwmp2p.client.HWMP2PClient;
import com.aviacomm.hwmp2p.client.MessageEnum;
import com.aviacomm.hwmp2p.sensor.zephyr.BioHarnessManager;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/*
 * Section Two:Display Section
 */
public class ScreenAdjustFragment extends Fragment {
	View view;
	Context context;
	private final int HEART_RATE = 0x100;
	private final int RESPIRATION_RATE = 0x101;
	private final int SKIN_TEMPERATURE = 0x102;
	private final int POSTURE = 0x103;
	private final int PEAK_ACCLERATION = 0x104;
	ChartView breathingview;
	ChartView temperatureview;
	TextView heartnumber;
	BioHarnessManager bioHarnessManager;
	TextView lowbattery;
	TextView critialdanger;
	TextView outofrange;

	public ScreenAdjustFragment(Context context) {
		super();
		this.context = context;
		initModule();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_display, container, false);
		breathingview = (ChartView) view
				.findViewById(R.id.display_breathing_chart);
		breathingview.setCOLOR(Color.YELLOW);
		temperatureview = (ChartView) view
				.findViewById(R.id.display_temperature_chart);
		heartnumber = (TextView) view.findViewById(R.id.display_heartnumber);
		lowbattery = (TextView) view.findViewById(R.id.display_lowbattery);

		critialdanger = (TextView) view
				.findViewById(R.id.display_criticaldanger);

		outofrange = (TextView) view.findViewById(R.id.display_outofrange);

		return view;
	}

	public void onStart() {
		super.onStart();
	}

	public void initModule() {
		if (HWMP2PClient.HASBLUETOOTH) {
			bioHarnessManager = new BioHarnessManager(displayhandler, context);
			bioHarnessManager.start();
		}
	}

	public void onResume() {
		super.onResume();
	}

	public Handler getHandler() {
		return displayhandler;
	}

	int criticalWarnstate = 0;
	int tempWarnColor = Color.WHITE;
	Runnable criticalWarnTask = new Runnable() {
		@Override
		public void run() {
			if (criticalWarnstate == 1) {
				tempWarnColor = tempWarnColor == Color.WHITE ? Color.RED
						: Color.WHITE;
				critialdanger.setTextColor(tempWarnColor);
				displayhandler.postDelayed(this, 200);
			}
		}
	};

	private void checkCriticalWarn() {
		ClientConfig config = HWMP2PClient.clientConfig;
		int minheart = Integer.valueOf(config.getValue("minheart"));
		int maxheart = Integer.valueOf(config.getValue("maxheart"));
		int minbreath = Integer.valueOf(config.getValue("minbreath"));
		int maxbreath = Integer.valueOf(config.getValue("maxbreath"));
		float temperature = Float.valueOf(config.getValue("temperature"));

		if (i_heart < minheart || i_heart > maxheart || i_breath < minbreath
				|| i_breath > maxbreath || i_temperature < temperature) {
			if (criticalWarnstate == 0) {
				criticalWarnstate = 1;
				displayhandler.post(criticalWarnTask);
			}
		} else {
			criticalWarnstate = 0;
		}
	}

	public static int i_heart = 0;
	public static float i_breath = 0;
	public static float i_temperature = 0;
	final Handler displayhandler = new Handler() {
		public void handleMessage(Message msg) {
			TextView tv;
			switch (msg.what) {
			case HEART_RATE:
				if (heartnumber != null) {
					i_heart = Integer.valueOf(msg.getData().getString(
							"HeartRate"));
					heartnumber.setText(i_heart + "");
					checkCriticalWarn();
				}
				break;
			case RESPIRATION_RATE:
				if (breathingview != null) {
					i_breath = Float.valueOf(msg.getData().getString(
							"RespirationRate"));
					breathingview.addOneData(i_breath);
					breathingview.updateView();
					checkCriticalWarn();
				}
				break;

			case SKIN_TEMPERATURE:
				if (temperatureview != null) {
					i_temperature = Float.valueOf(msg.getData().getString(
							"SkinTemperature"));
					temperatureview.addOneData(i_temperature);
					temperatureview.updateView();
					checkCriticalWarn();
				}
				break;
			case MessageEnum.LOWBATTERYWARN:
				if (lowbattery != null) {
					if (msg.arg1 == 0)
						lowbattery.setTextColor(Color.RED);
					if (msg.arg1 == 1)
						lowbattery.setTextColor(Color.WHITE);
				}
				break;
			case MessageEnum.CRITICALDANGERWARN:
				if (critialdanger != null) {
					if (msg.arg1 == 0)
						critialdanger.setTextColor(Color.RED);
					if (msg.arg1 == 1)
						critialdanger.setTextColor(Color.WHITE);
				}
				break;
			case MessageEnum.OUTOFRANGEWARN:
				if (outofrange != null) {
					if (msg.arg1 == 0)
						outofrange.setTextColor(Color.RED);
					if (msg.arg1 == 1)
						outofrange.setTextColor(Color.WHITE);
				}
				break;
			}
		}
	};
}
