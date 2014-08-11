package com.aviacomm.hwmp2p.ui;

import com.aviacomm.hwmp2p.R;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * Section Three: Action Section
 */
public class ActionPageFragment extends Fragment {
	View view;
	Context context;
	ImageView walk_button;
	ImageView urgent_button;
	ImageView video_button;
	ActionPageListener actionPageListener;

	public ActionPageFragment(Context context, ActionPageListener listener) {
		super();
		this.context = context;
		this.actionPageListener = listener;
	}

	public static int urgent = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_action, container, false);
		// BatteryView bbiew=new BatteryView(this.getActivity(), null,
		// TRIM_MEMORY_BACKGROUND);
		walk_button = (ImageView) view.findViewById(R.id.walk_button);
		urgent_button = (ImageView) view.findViewById(R.id.urgen_button);
		urgent_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				urgent = urgent == 0 ? 1 : 0;
				urgent_button.setImageLevel(urgent);
			}
		});
		video_button = (ImageView) view.findViewById(R.id.video_button);
		OnClickListener notfinish = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Not Finished!", Toast.LENGTH_SHORT)
						.show();
			}
		};
		video_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				actionPageListener.onClickActionPageButton(
						ActionPageListener.BUTTON_VIDEO, null);

			}
		});
		walk_button.setOnClickListener(notfinish);
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

	public interface ActionPageListener {
		public static final int BUTTON_VIDEO = 1;

		public void onClickActionPageButton(int but_id, Object obj);
	}
}
