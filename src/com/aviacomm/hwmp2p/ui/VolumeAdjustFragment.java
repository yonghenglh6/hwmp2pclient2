package com.aviacomm.hwmp2p.ui;


import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.client.MessageEnum;
import com.aviacomm.hwmp2p.sensor.MusicVolumeManager;
import com.aviacomm.hwmp2p.team.ConnectionManager;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
/*
 * Discarded for now!
 * It appeared in "HWM P2P (Client27).pdf", disappeared in "HWM P2P GUI Description 7-24.pdf"
 */
public class VolumeAdjustFragment extends Fragment {
	private View view;
	ProgressBar volume;
	VolumeAdjustListener listener;
	MusicVolumeManager musicVolumeManager;
	Context context;
	public VolumeAdjustFragment(Context context,VolumeAdjustListener listener){
		super();
		this.listener=listener;
		this.context=context;
	}
	int initialVolume;
	@Override
	public void onStart() {
		super.onStart();
		initModule();
	}
	public void initModule() {
		musicVolumeManager=new MusicVolumeManager(context);
		initialVolume=musicVolumeManager.getCurrentVolume();
		View save=view.findViewById(R.id.volumeadjust_save);
        View cancel=view.findViewById(R.id.volumeadjust_cancel);
        View increase=view.findViewById(R.id.volumeadjust_volume_increase);
        View decrease=view.findViewById(R.id.volumeadjust_volume_decrease);
        volume = (ProgressBar) view.findViewById(R.id.volumeadjust_volume_progressbar);
        save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				listener.onClickVolumeAdjustButton(VolumeAdjustListener.BUTTON_SAVE, null);
			}
		});
        cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				musicVolumeManager.setVolume(initialVolume);
				listener.onClickVolumeAdjustButton(VolumeAdjustListener.BUTTON_CANCEL, null);
			}
		});
        increase.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				musicVolumeManager.increaseVolume();
				volume.setProgress(musicVolumeManager.getVolumeLevel());
			}
		});
        decrease.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				musicVolumeManager.decreaseVolume();
				volume.setProgress(musicVolumeManager.getVolumeLevel());
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		volume.setProgress(musicVolumeManager.getVolumeLevel());
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_volumeadjust, container, false);
       
        return view;
    }
    

	public interface VolumeAdjustListener {
		public static final int BUTTON_SAVE = 1;
		public static final int BUTTON_CANCEL = 2;
		public void onClickVolumeAdjustButton(int buttonId, Object obj);
	}
}
