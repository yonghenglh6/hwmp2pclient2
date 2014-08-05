package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.ClientConfig;
import com.aviacomm.hwmp2p.R;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ClientConfigFragment extends Fragment {
	Context context;
	View view;
	ImageView cfbutton;
	View save,cancel;
	ClientConfigListener listener;
	public ClientConfigFragment(Context context,ClientConfigListener listener) {
		super();
		this.listener=listener;
		this.context = context;
	}
	int currentUnit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_config, container, false);
		cfbutton = (ImageView) view.findViewById(R.id.config_cf_button);
		save=(View)view.findViewById(R.id.config_save);
		cancel=(View)view.findViewById(R.id.config_cancel);
		currentUnit=ClientConfig.TemperatureUnit;
		cfbutton.setImageLevel(currentUnit);
		cfbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				currentUnit = currentUnit == ClientConfig.CELSIUS ? ClientConfig.FAHRENHEIT
						: ClientConfig.CELSIUS;
				cfbutton.setImageLevel(currentUnit);
			}
		});
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickClientConfigButton(ClientConfigListener.BUTTON_SAVE, null);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickClientConfigButton(ClientConfigListener.BUTTON_CANCEL, null);
			}
		});
		return view;
	}

	public void onStart() {
		super.onStart();
	}

	public interface ClientConfigListener {
		public static final int BUTTON_SAVE = 1;
		public static final int BUTTON_CANCEL = 2;

		public void onClickClientConfigButton(int buttonId, Object obj);
	}
}
