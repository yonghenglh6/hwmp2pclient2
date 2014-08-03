package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.MessageEnum;
import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.R.id;
import com.aviacomm.hwmp2p.R.layout;
import com.aviacomm.hwmp2p.team.ConnectionManager;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

/*
 * Section one:
 * Icon Section
 */
public class MainPageFragment extends Fragment {
	public final String TAG="MainPageFragment";
	View view;
	ProgressBar battery;
	ProgressBar volume;
	ImageView compass_pointer;
	MainPageListener listener;
	ConnectionManager cmanager;
	ImageButton back;
	ImageView wifi_intensity;
	Button scan;
	Button createTeam;
	Button increaseVolume;
	Button decreaseVolume;
	int currentStreamVolume = 0;

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
		initModule();
		return view;
	}

	public void initModule() {
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
		wifi_intensity = (ImageView) view.findViewById(R.id.wifiIntensity);
		scan = (Button) view.findViewById(R.id.apselector_cancel);
		createTeam = (Button) view.findViewById(R.id.apselector_connect);
		increaseVolume = (Button) view.findViewById(R.id.button_incvolume);
		decreaseVolume = (Button) view.findViewById(R.id.button_decvolume);
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
				AudioManager mAudioManager = (AudioManager) MainPageFragment.this
						.getActivity().getSystemService(Context.AUDIO_SERVICE);
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE,
					0);
				updateCurrentVolumeLevel();
			}
		});
		decreaseVolume.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AudioManager mAudioManager = (AudioManager) MainPageFragment.this
						.getActivity().getSystemService(Context.AUDIO_SERVICE);
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER,0);
				updateCurrentVolumeLevel();
			}
		});
		// wifi_intensity.setImageResource(R.drawable.wifi_intensity_levellist);
		// wifi_intensity.setImageLevel(3);
	}

	private int updateCurrentVolumeLevel(){
		AudioManager mAudioManager = (AudioManager) MainPageFragment.this
				.getActivity().getSystemService(Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int level = current * 100 / max;
		Log.i(TAG,"music volume is "+level);
		volume.setProgress(level);
		return level;
	}
	@Override
	public void onResume() {
		super.onResume();
		updateCurrentVolumeLevel();
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
