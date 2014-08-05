package com.aviacomm.hwmp2p;

import com.aviacomm.hwmp2p.sensor.SensorManager;
import com.aviacomm.hwmp2p.team.ConnectionManager;
import com.aviacomm.hwmp2p.team.MWifiDirectAP;
import com.aviacomm.hwmp2p.ui.APSelectorFragment;
import com.aviacomm.hwmp2p.ui.APSelectorFragment.ApSelectorListener;
import com.aviacomm.hwmp2p.ui.ActionPageFragment;
import com.aviacomm.hwmp2p.ui.ClientConfigFragment;
import com.aviacomm.hwmp2p.ui.ClientConfigFragment.ClientConfigListener;
import com.aviacomm.hwmp2p.ui.DisplayPageFragment;
import com.aviacomm.hwmp2p.ui.MainPageFragment;
import com.aviacomm.hwmp2p.ui.MainPageFragment.MainPageListener;
import com.aviacomm.hwmp2p.ui.VolumeAdjustFragment;
import com.aviacomm.hwmp2p.ui.VolumeAdjustFragment.VolumeAdjustListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * MainActivity. 
 * Respond to most UI interaction of user from the fragments, and hand out the message from other threads to the fragments.
 */
public class HWMP2PClient extends Activity implements
		ConnectionManager.ConnectionManagerListener, Handler.Callback,
		MainPageListener, ApSelectorListener, VolumeAdjustListener,
		ClientConfigListener {

	public static MLog log;
	// StartPageFragment startpageFragment;
	ViewGroup rootContent;
	// ViewGroup displayContent;
	// ViewGroup actionContent;
	MainPageFragment mainpageFragment;
	public static ClientConfig clientConfig = new ClientConfig();
	DisplayPageFragment displaypageFragment;
	ActionPageFragment actionpageFragment;
	ConnectionManager cmanager;
	public static Handler handler;
	Fragment currentFragmentInRootContent;
	APSelectorFragment apSelectorFragment;
	TextView statusView;
	SensorManager sensormanager;
	VolumeAdjustFragment volumeAdjustFragment;
	ClientConfigFragment clientConfigFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hwmp2_pclient);
		handler = new Handler(this);
		log = new MLog(handler);
		statusView = (TextView) this.findViewById(R.id.stateText);
		cmanager = new ConnectionManager(this, this, handler);
		cmanager.initial();
		sensormanager = new SensorManager(this, getHandler());

		// init three fragment in the main activity
		mainpageFragment = new MainPageFragment(this, this);
		displaypageFragment = new DisplayPageFragment(this);
		actionpageFragment = new ActionPageFragment(this);
		apSelectorFragment = new APSelectorFragment(this, this);
		volumeAdjustFragment = new VolumeAdjustFragment(this, this);
		clientConfigFragment = new ClientConfigFragment(this, this);
		rootContent = (ViewGroup) findViewById(R.id.rootContent);

		showSingleFragmentInRootContent(mainpageFragment);
		getFragmentManager().beginTransaction()
				.add(R.id.displayContent, displaypageFragment).commit();
		getFragmentManager().beginTransaction()
				.add(R.id.actionContent, actionpageFragment).commit();
		// start Sensor
		// startpageFragment = new StartPageFragment(this);
		// show startPage
		// showSingleFragmentInRootContent(startpageFragment);
	}

	public ConnectionManager getConnectionManager() {
		return cmanager;
	}

	private void showSingleFragmentInRootContent(Fragment page) {
		if (currentFragmentInRootContent == page)
			return;
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		if (currentFragmentInRootContent != null)
			transaction.remove(currentFragmentInRootContent);
		transaction.add(R.id.rootContent, page);
		// transaction.addToBackStack(null);
		transaction.commit();
		currentFragmentInRootContent = page;
	}

	public Handler getHandler() {
		return handler;
	}

	private void addStatusText(String text) {
		statusView.append(text);
		((ScrollView) findViewById(R.id.stateTextScroll))
				.fullScroll(ScrollView.FOCUS_DOWN);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MessageEnum.BATTERYCHANGE:
		case MessageEnum.VOLUMECHANGE:
		case MessageEnum.ORIENTATIONCHANGE:
		case MessageEnum.WIFIINTENSITYCHANGE:
		case MessageEnum.CONNECTIONBROKEN:
			mainpageFragment.handleMessage(msg);
			break;

		case MessageEnum.LOGMESSAGE:
			addStatusText((String) msg.obj);

			break;
		case MessageEnum.WIFIAPDISCOVED:
			apSelectorFragment.handleMessage(msg);
			break;
		case MessageEnum.CONNECTIONESTABLISHED:
			showSingleFragmentInRootContent(mainpageFragment);
			mainpageFragment.handleMessage(msg);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClickAPSelectorButton(int buttonId, Object obj) {
		switch (buttonId) {
		case ApSelectorListener.BUTTON_CANCEL:
			showSingleFragmentInRootContent(mainpageFragment);
			break;
		case ApSelectorListener.BUTTON_CONNECT:
			cmanager.connect((MWifiDirectAP) obj);
			break;
		case ApSelectorListener.BUTTON_RESCAN:
			cmanager.discoverTeamService();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensormanager.stopall();
		cmanager.stop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensormanager.startall();
		cmanager.start();
	}

	@Override
	public void onClickMainPageButton(int buttonId, Object obj) {
		switch (buttonId) {
		case MainPageListener.BUTTON_BACK:
			Toast.makeText(this, "where do you want go back to?",
					Toast.LENGTH_SHORT).show();
			break;
		case MainPageListener.BUTTON_SCAN:
			showSingleFragmentInRootContent(apSelectorFragment);
			cmanager.discoverTeamService();
			break;
		case MainPageListener.BUTTON_CREATETEAM:
			cmanager.createTeamService();
			break;
		case MainPageListener.VOLUMEADJUST:
			showSingleFragmentInRootContent(volumeAdjustFragment);
			break;
		case MainPageListener.CONFIG:
			showSingleFragmentInRootContent(clientConfigFragment);
			break;
		default:
			break;
		}
	}

	@Override
	public void onInvokeConnectionEvent(int eventId, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickVolumeAdjustButton(int buttonId, Object obj) {
		// TODO Auto-generated method stub
		showSingleFragmentInRootContent(mainpageFragment);
	}

	@Override
	public void onClickClientConfigButton(int buttonId, Object obj) {
		// TODO Auto-generated method stub
		switch (buttonId) {
		case ClientConfigListener.BUTTON_SAVE:
			showSingleFragmentInRootContent(mainpageFragment);
			break;
		case ClientConfigListener.BUTTON_CANCEL:
			showSingleFragmentInRootContent(mainpageFragment);
			break;
		}
	}

}
