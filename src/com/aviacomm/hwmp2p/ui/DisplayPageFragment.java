package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.R;
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
public class DisplayPageFragment extends Fragment {
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

	public DisplayPageFragment(Context context) {
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
		return view;
	}

	public void onStart() {
		super.onStart();
	}

	public void initModule() {
		bioHarnessManager = new BioHarnessManager(displayhandler, context);
	}

	public void onResume() {
		super.onResume();
		bioHarnessManager.connect();
	}

	final Handler displayhandler = new Handler() {
		public void handleMessage(Message msg) {
			TextView tv;
			switch (msg.what) {
			case HEART_RATE:
				if (heartnumber != null)
					heartnumber.setText(msg.getData().getString("HeartRate"));
				break;
			case RESPIRATION_RATE:
				if (breathingview != null) {
					breathingview.addOneData(Float.valueOf(msg.getData()
							.getString("RespirationRate")));
					breathingview.updateView();
				}
				break;

			case SKIN_TEMPERATURE:
				if (temperatureview != null) {
					temperatureview.addOneData(Float.valueOf(msg.getData()
							.getString("SkinTemperature")));
					temperatureview.updateView();
				}
				break;

			}
		}

	};
}
