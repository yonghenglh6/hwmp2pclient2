package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.client.MessageEnum;
import com.aviacomm.hwmp2p.sensor.MusicVolumeManager;
import com.aviacomm.hwmp2p.team.ConnectionManager;
import com.aviacomm.hwmp2p.team.WifiStateManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * Section one:
 * Icon Section
 */
public class MainPageFragment extends Fragment {
	public final String TAG = "MainPageFragment";
	View view;
	ProgressBar battery;
	ProgressBar volume;
	ImageView compass_pointer;
	ImageView connection_establish_indicator;
	MainPageListener listener;
	// ConnectionManager cmanager;
	ImageButton back;
	ImageView wifi_intensity;
	Button scan;
	Button createTeam;
	Button resetwifi;
	ImageView config;
	int currentStreamVolume = 0;
	int currentBatteryLevel = 0;
	MusicVolumeManager musicVolumeManager;
	WifiStateManager wifiStateManager;
	TextView batteryLevelText;
	Context context;

	public MainPageFragment(Context context, MainPageListener listener) {
		super();
		this.context = context;
		this.listener = listener;
		// this.cmanager = cmanager;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_main, container, false);
		// BatteryView bbiew=new BatteryView(this.getActivity(), null,
		// TRIM_MEMORY_BACKGROUND);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		initModule();
	}

	public void initModule() {

		musicVolumeManager = new MusicVolumeManager(context);
		wifiStateManager = new WifiStateManager(getActivity());
		battery = (ProgressBar) view.findViewById(R.id.batteryProgressBar);
		volume = (ProgressBar) view.findViewById(R.id.volumeProgressBar);
		compass_pointer = (ImageView) view.findViewById(R.id.compass_pointer);
		back = (ImageButton) view.findViewById(R.id.mainpage_back_button);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// listener.onClickMainPageButton(MainPageListener.BUTTON_BACK,
				// null);
			}
		});
		config = (ImageView) view.findViewById(R.id.mainpage_config_button);
		batteryLevelText = (TextView) view
				.findViewById(R.id.mainpage_battery_number);
		connection_establish_indicator = (ImageView) view
				.findViewById(R.id.mainpage_connection_establish_indicator);
		wifi_intensity = (ImageView) view.findViewById(R.id.wifiIntensity);
		scan = (Button) view.findViewById(R.id.mainpage_scan);
		createTeam = (Button) view.findViewById(R.id.mainpage_createteam);
		resetwifi = (Button) view.findViewById(R.id.mainpage_resetwifi);
		scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickMainPageButton(MainPageListener.BUTTON_SCAN,
						null);
			}
		});
		createTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickMainPageButton(
						MainPageListener.BUTTON_CREATETEAM, null);
			}
		});
		resetwifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				wifiStateManager.resetWifi();
			}
		});
		config.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickMainPageButton(MainPageListener.CONFIG, null);
			}
		});
		// wifi_intensity.setImageResource(R.drawable.wifi_intensity_levellist);
		// wifi_intensity.setImageLevel(3);
		View volumeadjustLayout = view
				.findViewById(R.id.mainpage_volumeadjust_layout);
		volumeadjustLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickMainPageButton(MainPageListener.VOLUMEADJUST,
						null);
			}
		});
	}

	private boolean isConnectionEstablished = false;

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	private void updateUI() {
		volume.setProgress(musicVolumeManager.getVolumeLevel());
		connection_establish_indicator.setAlpha(isConnectionEstablished ? 0f
				: 1f);
		battery.setProgress(currentBatteryLevel);
		batteryLevelText.setText(currentBatteryLevel + "%");
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MessageEnum.BATTERYCHANGE:
			if (battery != null) {
				currentBatteryLevel = msg.arg1;
				updateUI();
			}

			break;
		case MessageEnum.VOLUMECHANGE:
			if (volume != null)
				volume.setProgress(msg.arg1);
			break;
		case MessageEnum.ORIENTATIONCHANGE:
			if (compass_pointer != null)
				compass_pointer.setRotation(360 - (Float) msg.obj);
			break;
		case MessageEnum.WIFIINTENSITYCHANGE:
			if (wifi_intensity != null)
				wifi_intensity.setImageLevel(msg.arg1);
			break;
		case MessageEnum.CONNECTIONESTABLISHED:
			if (connection_establish_indicator != null) {
				isConnectionEstablished = true;
				connection_establish_indicator.setAlpha(0f);
				// HWMP2PClient.log.i("it should be invisible");
			}
			break;
		case MessageEnum.CONNECTIONBROKEN:
			if (connection_establish_indicator != null) {
				isConnectionEstablished = false;
				connection_establish_indicator.setAlpha(1f);
			}
			break;
		default:
			break;
		}
	}

	public interface MainPageListener {
		public static final int BUTTON_BACK = 1;
		public static final int BUTTON_SCAN = 2;
		public static final int BUTTON_CREATETEAM = 3;
		public static final int VOLUMEADJUST = 4;
		public static final int CONFIG = 5;

		public void onClickMainPageButton(int buttonId, Object obj);
	}
}
