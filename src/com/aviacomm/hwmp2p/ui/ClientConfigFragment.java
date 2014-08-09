package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.client.ClientConfig;
import com.aviacomm.hwmp2p.client.HWMP2PClient;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ClientConfigFragment extends Fragment {
	Context context;
	View view;
	ImageView cfbutton;
	View save, cancel;
	ClientConfigListener listener;
	SharedPreferences shp;
	EditText value_texts[];
	int uiID_values[] = { R.id.config_devicename, R.id.config_id,
			R.id.config_minheart, R.id.config_maxheart, R.id.config_minbreath,
			R.id.config_maxbreath, R.id.config_temperature };
	String attrname[] = { "devicename", "deviceid", "minheart", "maxheart",
			"minbreath", "maxbreath", "temperature" };

	public ClientConfigFragment(Context context, ClientConfigListener listener) {
		super();
		this.listener = listener;
		this.context = context;
		shp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	int currentUnit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_config, container, false);
		cfbutton = (ImageView) view.findViewById(R.id.config_cf_button);
		save = (View) view.findViewById(R.id.config_save);
		cancel = (View) view.findViewById(R.id.config_cancel);
		currentUnit = ClientConfig.TemperatureUnit;
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
				saveAllValue();
				listener.onClickClientConfigButton(
						ClientConfigListener.BUTTON_SAVE, null);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				initAllvalue();
				listener.onClickClientConfigButton(
						ClientConfigListener.BUTTON_CANCEL, null);
			}
		});
		initAllvalue();
		return view;
	}

	private void initAllvalue() {
		for (int i = 0; i < attrname.length; i++) {
			String storedvalue = HWMP2PClient.clientConfig
					.getValue(attrname[i]);
			EditText text = (EditText) view.findViewById(uiID_values[i]);
			if (text == null)
				continue;
			text.setText(storedvalue);
		}
	}

	private void saveAllValue() {
		for (int i = 0; i < attrname.length; i++) {
			EditText text = (EditText) view.findViewById(uiID_values[i]);
			if (text == null)
				continue;
			String editvalue = text.getText().toString();
			HWMP2PClient.clientConfig.setValue(attrname[i], editvalue);

		}
	}

	public void onStart() {
		super.onStart();
		// for (int i = 0; i < attrname.length; i++) {
		// String storedvalue = shp.getString(attrname[i], "NOTSET");
		// EditText text = (EditText) view.findViewById(uiID_values[i]);
		// text.setText(storedvalue);
		// }
	}

	public interface ClientConfigListener {
		public static final int BUTTON_SAVE = 1;
		public static final int BUTTON_CANCEL = 2;

		public void onClickClientConfigButton(int buttonId, Object obj);
	}
}
