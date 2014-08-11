package com.aviacomm.hwmp2p.client;

import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.client.connection.ClientConnection;
import com.aviacomm.hwmp2p.connection.TeamClientList;
import com.aviacomm.hwmp2p.connection.TeamControl;
import com.aviacomm.hwmp2p.sensor.SensorManager;
import com.aviacomm.hwmp2p.team.ConnectionManager;
import com.aviacomm.hwmp2p.team.MLog;
import com.aviacomm.hwmp2p.team.MWifiDirectAP;
import com.aviacomm.hwmp2p.team.TeamManager;
import com.aviacomm.hwmp2p.ui.APSelectorFragment;
import com.aviacomm.hwmp2p.ui.APSelectorFragment.ApSelectorListener;
import com.aviacomm.hwmp2p.ui.ActionPageFragment;
import com.aviacomm.hwmp2p.ui.ActionPageFragment.ActionPageListener;
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
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
		ClientConfigListener, ActionPageListener {
	public static boolean DEBUG = false;
	public static boolean HASBLUETOOTH = true;
	public static MLog log;
	// StartPageFragment startpageFragment;
	ViewGroup rootContent;
	// ViewGroup displayContent;
	// ViewGroup actionContent;
	MainPageFragment mainpageFragment;
	public static ClientConfig clientConfig;
	DisplayPageFragment displaypageFragment;
	ActionPageFragment actionpageFragment;
	// ConnectionManager cmanager;
	TeamManager teamManager;
	public static Handler handler;
	Fragment currentFragmentInRootContent;
	APSelectorFragment apSelectorFragment;
	TextView statusView;
	SensorManager sensormanager;
	VolumeAdjustFragment volumeAdjustFragment;
	ClientConfigFragment clientConfigFragment;
	TeamControl teamControl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hwmp2_pclient);
		statusView = (TextView) this.findViewById(R.id.stateText);
		if(DEBUG){
			statusView.setVisibility(View.VISIBLE);
		}else{
			statusView.setVisibility(View.GONE);
		}
		clientConfig = new ClientConfig(this);
		handler = new Handler(this);
		log = new MLog(handler);
		teamManager = new TeamManager(this, handler, log);
		teamManager.initial();
		teamControl = new TeamControl();
		// cmanager = new ConnectionManager(this, this, handler);
		// cmanager.initial();
		sensormanager = new SensorManager(this, getHandler());

		// init three fragment in the main activity
		mainpageFragment = new MainPageFragment(this, this);
		displaypageFragment = new DisplayPageFragment(this);
		actionpageFragment = new ActionPageFragment(this, this);
		apSelectorFragment = new APSelectorFragment(this, this);
		volumeAdjustFragment = new VolumeAdjustFragment(this, this);
		clientConfigFragment = new ClientConfigFragment(this, this);
		rootContent = (ViewGroup) findViewById(R.id.rootContent);
		clientConnection = new ClientConnection();
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

	public boolean isConnected() {
		return teamManager.isConnected();
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
		if (statusView != null && text != null) {
			statusView.append(text);
			((ScrollView) findViewById(R.id.stateTextScroll))
					.fullScroll(ScrollView.FOCUS_DOWN);
		}
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

		case MLog.LOGMESSAGE:
			addStatusText((String) msg.obj);

			break;
		case MessageEnum.WIFIAPDISCOVED:
			apSelectorFragment.handleMessage(msg);
			break;
		case MessageEnum.CONNECTIONESTABLISHED:
			showSingleFragmentInRootContent(mainpageFragment);
			mainpageFragment.handleMessage(msg);
			break;
		case MessageEnum.LOWBATTERYWARN:
		case MessageEnum.CRITICALDANGERWARN:
		case MessageEnum.OUTOFRANGEWARN:
			displaypageFragment.getHandler().handleMessage(msg);
			break;
		case TeamManager.WHAT_GROUPFORMED:
			String listenPort = teamManager.getListenPort();
			teamControl.init((WifiP2pInfo) msg.obj, listenPort,
					this.getHandler(), "client");
			break;
		case TeamManager.WHAT_CONNECTIONESTABLISHED:
			showSingleFragmentInRootContent(mainpageFragment);
			mainpageFragment.handleMessage(msg);
			break;
		case TeamManager.WHAT_CONNECTIONBROKEN:
			mainpageFragment.handleMessage(msg);
			break;
		case TeamManager.WHAT_WIFIAPDISCOVED:
			apSelectorFragment.handleMessage(msg);
			break;
		case TeamControl.WAHT_INTRODUCE_MYSELF:
			clientConnection.setTeamClientList(teamControl.getList());
			clientConnection.init(msg.arg1);
			break;
		case TeamClientList.CLIENTADD:
			clientConnection.clientListChanged();
			break;
		default:
			break;
		}
		return false;
	}

	ClientConnection clientConnection;

	@Override
	public void onClickAPSelectorButton(int buttonId, Object obj) {
		switch (buttonId) {
		case ApSelectorListener.BUTTON_CANCEL:
			showSingleFragmentInRootContent(mainpageFragment);
			break;
		case ApSelectorListener.BUTTON_CONNECT:
			teamManager.connect((MWifiDirectAP) obj);
			break;
		case ApSelectorListener.BUTTON_RESCAN:
			teamManager.discoverTeam();
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
		// cmanager.stop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensormanager.startall();
		// cmanager.start();
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
			teamManager.discoverTeam();
			break;
		case MainPageListener.BUTTON_CREATETEAM:
			teamManager.createTeam();
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

	RelativeLayout videodialog;

	@Override
	public void onClickActionPageButton(int but_id, Object obj) {
		// TODO Auto-generated method stub
		switch (but_id) {
		case ActionPageListener.BUTTON_VIDEO:
			videodialog = (RelativeLayout) findViewById(R.id.vediodialogLayout);
			LayoutInflater.from(this).inflate(R.layout.videoselector,
					videodialog);
			videodialog.setVisibility(View.VISIBLE);
			int button_to_set[] = { R.id.video_button_name1,
					R.id.video_button_name2, R.id.video_button_name3,
					R.id.video_button_name4, R.id.video_button_name5,
					R.id.video_button_name6, R.id.video_button_name7,
					R.id.video_button_name8, R.id.video_button_name9 };
			for (int i = 0; i < button_to_set.length; i++) {
				TextView name = (TextView) videodialog
						.findViewById(button_to_set[i]);
				name.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Toast.makeText(HWMP2PClient.this, "Not Finished!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			TextView cancel = (TextView) videodialog
					.findViewById(R.id.video_cancel_button);
			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					videodialog.setVisibility(View.GONE);
				}
			});
			break;
		}
	}

}
