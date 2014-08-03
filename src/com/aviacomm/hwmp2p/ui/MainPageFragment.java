package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.HWMP2PClient;
import com.aviacomm.hwmp2p.MessageEnum;
import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.R.id;
import com.aviacomm.hwmp2p.R.layout;
import com.aviacomm.hwmp2p.sensor.MusicVolumeManager;
import com.aviacomm.hwmp2p.team.ConnectionManager;
import com.aviacomm.hwmp2p.team.WifiStateManager;
import com.aviacomm.hwmp2p.ui.StartPageFragment.StartPageListener;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
	ConnectionManager cmanager;
	ImageButton back;
	ImageView wifi_intensity;
	Button scan;
	Button createTeam;
	Button increaseVolume;
	Button decreaseVolume;
	Button resetwifi;
	int currentStreamVolume = 0;
	MusicVolumeManager musicVolumeManager;
	WifiStateManager wifiStateManager;

	public MainPageFragment(ConnectionManager cmanager,
			MainPageListener listener) {
		super();
		this.listener = listener;
		this.cmanager = cmanager;
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

		musicVolumeManager = new MusicVolumeManager(getActivity());
		wifiStateManager = new WifiStateManager(getActivity());
		battery = (ProgressBar) view.findViewById(R.id.batteryProgressBar);
		volume = (ProgressBar) view.findViewById(R.id.volumeProgressBar);
		compass_pointer = (ImageView) view.findViewById(R.id.compass_pointer);
		back = (ImageButton) view.findViewById(R.id.mainpage_back_button);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				listener.onClickMainPageButton(MainPageListener.BUTTON_BACK,
						null);
			}
		});
		connection_establish_indicator = (ImageView) view
				.findViewById(R.id.mainpage_connection_establish_indicator);
		wifi_intensity = (ImageView) view.findViewById(R.id.wifiIntensity);
		scan = (Button) view.findViewById(R.id.mainpage_scan);
		createTeam = (Button) view.findViewById(R.id.mainpage_createteam);
		increaseVolume = (Button) view.findViewById(R.id.mainpage_incvolume);
		decreaseVolume = (Button) view.findViewById(R.id.mainpage_decvolume);
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
		increaseVolume.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				musicVolumeManager.increaseVolume();
				volume.setProgress(musicVolumeManager.getVolumeLevel());
			}
		});
		decreaseVolume.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				musicVolumeManager.decreaseVolume();
				volume.setProgress(musicVolumeManager.getVolumeLevel());
			}
		});
		resetwifi.setText(wifiStateManager.isWifiOn() ? "TurnOff WIFI"
				: "TurnON WIFI");
		resetwifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				wifiStateManager.resetWifi();
				resetwifi.setText(wifiStateManager.isWifiOn() ? "TurnOff WIFI"
						: "TurnON WIFI");
			}
		});
		// wifi_intensity.setImageResource(R.drawable.wifi_intensity_levellist);
		// wifi_intensity.setImageLevel(3);
	}

	private boolean isConnectionEstablished = false;

	@Override
	public void onResume() {
		super.onResume();
		volume.setProgress(musicVolumeManager.getVolumeLevel());
		connection_establish_indicator.setAlpha(isConnectionEstablished?0f:1f);
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MessageEnum.BATTERYCHANGE:
			if (battery != null)
				battery.setProgress(msg.arg1);
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
				HWMP2PClient.log.i("it should be invisible");
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

		public void onClickMainPageButton(int buttonId, Object obj);
	}
}
