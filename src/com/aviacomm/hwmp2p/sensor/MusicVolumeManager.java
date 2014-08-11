package com.aviacomm.hwmp2p.sensor;

import com.aviacomm.hwmp2p.client.HWMP2PClient;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/*
 * This is discarded!!!!!!!!         
 * It should be gotten when the UI show;
 */
public class MusicVolumeManager {
	// Handler handler;
	Context context;
	int currentStreamVolume = 0;
	public static final String TAG = "MusicVolumeManager";
	// IntentFilter filter = new IntentFilter();
	AudioManager mAudioManager;
	public final int streamType = AudioManager.STREAM_MUSIC;

	public MusicVolumeManager(Context context) {
		this.context = context;
		// this.handler = handler;
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	public void increaseVolume() {
		mAudioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_RAISE,
				0);
	}

	public void setVolume(int desvolume) {
		int n = desvolume - getCurrentVolume();
		int direction = n > 0 ? AudioManager.ADJUST_RAISE
				: AudioManager.ADJUST_LOWER;
		n = n > 0 ? n : -n;
		while (n-- > 0)
			mAudioManager.adjustStreamVolume(streamType, direction, 0);
	}

	public void decreaseVolume() {
		mAudioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER,
				0);
	}

	public int getCurrentVolume() {
		return mAudioManager.getStreamVolume(streamType);
	}

	public int getVolumeLevel() {
		int max = mAudioManager.getStreamMaxVolume(streamType);
		int current = mAudioManager.getStreamVolume(streamType);
		// HWMP2PClient.log.i("volue:"+current+"  "+ max);
		int level = current * 100 / max;
		Log.i(TAG, "music volume is " + level);
		return level;
	}
}
