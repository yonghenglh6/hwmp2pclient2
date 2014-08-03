package com.aviacomm.hwmp2p.sensor;

import com.aviacomm.hwmp2p.MessageEnum;
import com.aviacomm.hwmp2p.ui.MainPageFragment;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;

/*
 * This is discarded!!!!!!!!         
 * It should be gotten when the UI show;
 */
public class MusicVolumeManager {
//	Handler handler;
	Context context;
	int currentStreamVolume = 0;
	public static final String TAG="MusicVolumeManager";
	// IntentFilter filter = new IntentFilter();
	AudioManager mAudioManager;
	public final int streamType = AudioManager.STREAM_MUSIC;

	public MusicVolumeManager(Context context) {
		this.context = context;
//		this.handler = handler;
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public void increaseVolume() {
		mAudioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_RAISE,
				0);
	}

	public void decreaseVolume() {
		mAudioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER,
				0);
	}

	public int getVolumeLevel() {
		int max = mAudioManager.getStreamMaxVolume(streamType);
		int current = mAudioManager.getStreamVolume(streamType);
		int level = current * 100 / max;
		Log.i(TAG,"music volume is "+level);
		return level;
	}
}
