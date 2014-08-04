package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.MessageEnum;
import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.team.MWifiDirectAP;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
/*
 * Wireless connection AP select page.
 */
public class APSelectorFragment extends Fragment implements Callback {
	private View view;
	Button connect;
	Button cancel;
	Button rescan;
	RadioGroup apgroup;
	Context context;
	Handler apselectorHandler = new Handler(this);
	ApSelectorListener listener;

	public APSelectorFragment(Context context, ApSelectorListener listener) {
		super();
		this.context = context;
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater
				.inflate(R.layout.fragment_ap_selector, container, false);
		apgroup = (RadioGroup) view.findViewById(R.id.apGroup);
		connect = (Button) view.findViewById(R.id.apselector_connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				View selectedButton = apgroup.findViewById(apgroup
						.getCheckedRadioButtonId());
				if (selectedButton instanceof MAPRadioButton) {
					listener.onClickAPSelectorButton(
							ApSelectorListener.BUTTON_CONNECT, ((MAPRadioButton)selectedButton).getAp());
				}else{
					Toast.makeText(context, "Please check One AP!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancel = (Button) view.findViewById(R.id.apselector_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				listener.onClickAPSelectorButton(
						ApSelectorListener.BUTTON_CANCEL, null);
			}
		});
		rescan = (Button) view.findViewById(R.id.apselector_rescan);
		rescan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				apgroup.removeAllViews();
				listener.onClickAPSelectorButton(
						ApSelectorListener.BUTTON_RESCAN, null);
			}
		});

		return view;
	}

	public Handler getHandler() {
		return apselectorHandler;
	}

	public interface ApSelectorListener {
		public static final int BUTTON_CONNECT = 1;
		public static final int BUTTON_CANCEL = 2;
		public static final int BUTTON_RESCAN = 3;

		public void onClickAPSelectorButton(int but_id, Object obj);
	}

	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MessageEnum.WIFIAPDISCOVED:
			apgroup.addView(new MAPRadioButton(getActivity(),
					(MWifiDirectAP) msg.obj));
			break;
		default:
			break;
		}
		return false;
	}
	
	public class MAPRadioButton extends RadioButton {
		MWifiDirectAP ap;
		public MAPRadioButton(Context context, MWifiDirectAP ap) {
			super(context);
			this.ap = ap;
			this.setText(ap.device.deviceName);
		}
		public MWifiDirectAP getAp() {
			return ap;
		}
	}
}
