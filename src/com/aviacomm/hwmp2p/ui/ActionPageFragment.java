package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.R;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Section Three: Action Section
 */
public class ActionPageFragment extends Fragment {
	View view;
	Context context;

	public ActionPageFragment(Context context) {
		super();
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_action, container, false);
		// BatteryView bbiew=new BatteryView(this.getActivity(), null,
		// TRIM_MEMORY_BACKGROUND);
		return view;
	}

	public void initModule() {

	}

	public void handleMessage(Message msg) {
		switch (msg.what) {

		default:
			break;
		}
	}
}
