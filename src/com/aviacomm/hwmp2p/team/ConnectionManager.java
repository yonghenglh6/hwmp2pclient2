package com.aviacomm.hwmp2p.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aviacomm.hwmp2p.util.WifiDirectConnectionUitl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/*
 * This manages the Lower P2P connections.
 * In the future, this class will become a member of TeamManager.
 * and the main activity will not hold this but TeamManager.
 * 
 */
public class ConnectionManager implements GroupInfoListener {
	ConnectionManagerListener listener;
	public static boolean backupAp = false;
	private Channel mChannel;
	private WifiP2pManager mWifiP2pManager;
	// private TeamManager teamManager;
	// public static final String TXTRECORD_PROP_AVAILABLE = "available";
	// public static final String SERVICE_INSTANCE = "HWMP2PClient";
	Context context;
	WiFiDirectBroadcastReceiver receiver;
	private DnsSdServiceResponseListener dnsSdServiceResponseListener;
	private DnsSdTxtRecordListener dnsSdTxtRecordListener;
	public final String TAG = "ConnectionManager";
	public final String INSTANCENAME = "hwmp2p";
	public final String REGISTIONTYPE = "_presence._tcp";

	// Handler handler;
	List<MWifiDirectAP> aplist;
	@SuppressWarnings("unused")
	private boolean mWifiP2pEnabled;
	// private boolean mWifiP2pSearching;
	// private int mConnectedDevices;
	// private WifiP2pGroup mConnectedGroup;
	private boolean mLastGroupFormed = false;
	private WifiP2pDevice mThisDevice;
	public int SERVER_PORT;
	public String nickname;
	public MLog log = TeamManager.log;
	WifiStateManager wifiStateManager;

	private static final int BASE = 43659618;
	public static final int EVENT_WIFIAPDISCOVED = BASE + 1;
	public static final int EVENT_CONNECTIONESTABLISHED = BASE + 2;
	public static final int EVENT_CONNECTIONBROKEN = BASE + 3;
	public static final int EVENT_GROUPFORMED = BASE + 4;

	public ConnectionManager(Context context, ConnectionManagerListener listener) {
		this.context = context;
		this.listener = listener;
		// this.handler = handler;
	}

	// ！！！！ you must invoke initial function before any usage.
	public void initial() {
		aplist = new ArrayList<MWifiDirectAP>();
		mWifiP2pManager = (WifiP2pManager) context
				.getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mWifiP2pManager.initialize(context, context.getMainLooper(),
				null);
		wifiStateManager = new WifiStateManager(context);

		receiver = new WiFiDirectBroadcastReceiver();
		dnsSdServiceResponseListener = new DnsSdServiceResponseListener() {
			@Override
			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice srcDevice) {
				// A service has been discovered. Is this our app?
				log.i(TAG, "ServiceInstance Found");
				if (instanceName.equalsIgnoreCase(INSTANCENAME)) {
					MWifiDirectAP ap = MWifiDirectAP.getInstance(srcDevice,
							registrationType);
					Log.i("default","registType:"+registrationType+"   port:"+ap.listenPort);
					checkForList(ap);
				}
			}
		};

		dnsSdTxtRecordListener = new DnsSdTxtRecordListener() {
			@Override
			public void onDnsSdTxtRecordAvailable(String arg0,
					Map<String, String> record, WifiP2pDevice device) {
				log.i(TAG, "TXT record Found");
				MWifiDirectAP ap = MWifiDirectAP.getInstance(device,
						record.get("action"), record.get("gsignal"),
						record.get("nickname"), record.get("listenport"));
				checkForList(ap);
			}
		};
		mWifiP2pManager.setDnsSdResponseListeners(mChannel,
				dnsSdServiceResponseListener, dnsSdTxtRecordListener);

		SERVER_PORT = WifiDirectConnectionUitl.getAvailablePort(TeamManager.BASELISTENPORT);
		registerMe();
	}

	public void registerMe() {
		context.registerReceiver(receiver, receiver.getWifiDirectFilter());
		addServiceRequest();
	}

	public void unregisterMe() {
		context.unregisterReceiver(receiver);
	}

	WifiP2pDnsSdServiceRequest serviceRequest;

	private void addServiceRequest() {
		mWifiP2pManager.clearServiceRequests(mChannel,
				new ErrorSolutionActionListener(
						ErrorSolutionActionListener.CLEARSERVICEREQUEST));
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest,
				new ErrorSolutionActionListener(
						ErrorSolutionActionListener.OTHER,
						"add Request Service"));
	}

	// when found a device,check if stored before, if not ,add it to aplist
	public void checkForList(MWifiDirectAP ap) {
		synchronized (aplist) {
			MWifiDirectAP ori = null;
			for (int i = 0; i < aplist.size(); i++) {
				if (aplist.get(i).device.deviceAddress
						.equals(ap.device.deviceAddress)) {
					ori = aplist.get(i);
					break;
				}
			}
			if (ori == null) {
				// first find the device
				aplist.add(ap);
				listener.onInvokeConnectionEvent(EVENT_WIFIAPDISCOVED, ap);
				log.i(TAG, "A new device address:" + ap.device.deviceAddress);
			} else {
				int state = ori.Combine(ap);
				if (state == 1) {
					// An ap's info is complete! should be listed!
					// TODO
				}
			}
		}
	}

	public void connect(MWifiDirectAP ap) {
		log.i(TAG, "Invite :" + ap.device.deviceAddress);
		// teamManager.onStartConnect(ap);
		WifiP2pConfig config;
		config = new WifiP2pConfig();
		config.deviceAddress = ap.device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		config.groupOwnerIntent = 0;
		mWifiP2pManager.connect(mChannel, config,
				new ErrorSolutionActionListener(
						ErrorSolutionActionListener.CONNECT, "connect"));
	}

	public boolean isConnected() {
		return mLastGroupFormed;
	}

	public void discoverTeamService() {
		aplist.clear();
		mWifiP2pManager
				.discoverServices(mChannel, new ErrorSolutionActionListener(
						ErrorSolutionActionListener.DISCOVERY, "discover team"));
	}

	public void createTeamService() {
		Map<String, String> record = new HashMap<String, String>();
		Log.i("defalut",SERVER_PORT+"");
		record.put("action", "CREATETEAM");
		record.put("gsignal", WifiDirectConnectionUitl.generateGsignal());
		record.put("listenport", String.valueOf(SERVER_PORT));
		record.put(
				"nickname",
				nickname == null ? ("FireFighter" + (int) (Math.random() * 1000))
						: nickname);
		record.put("available", "visible");

		mWifiP2pManager
				.clearLocalServices(mChannel,
						new ErrorSolutionActionListener(
								ErrorSolutionActionListener.OTHER,
								"clearLocalServices"));

		String registionMsg = REGISTIONTYPE + ":" + SERVER_PORT;
		TeamManager.listenPort = SERVER_PORT + "";
		WifiP2pDnsSdServiceInfo addteamservice = WifiP2pDnsSdServiceInfo
				.newInstance(INSTANCENAME, registionMsg, record);
		mWifiP2pManager.addLocalService(mChannel, addteamservice,
				new ErrorSolutionActionListener(
						ErrorSolutionActionListener.ADDLOCALSERVICE,
						"create teamservice"));
		// mWifiP2pManager.createGroup(mChannel, new
		// ErrorSolutionActionListener(
		// ErrorSolutionActionListener.ADDLOCALSERVICE,
		// "create team group"));
		discoverTeamService();
	}

	boolean isFormedBefore=false;
	private void onConnectionEstablished() {
		log.i(TAG, "Connection is Established!");
		mWifiP2pManager.requestConnectionInfo(mChannel,
				new ConnectionInfoListener() {

					@Override
					public void onConnectionInfoAvailable(WifiP2pInfo arg0) {
						if (arg0.groupFormed) {
							listener.onInvokeConnectionEvent(
									EVENT_CONNECTIONESTABLISHED, null);
							if (!isFormedBefore) {
								log.i(TAG, "firstgroupFormed");
								isFormedBefore = true;
								listener.onInvokeConnectionEvent(
										EVENT_GROUPFORMED, arg0);
							} else {
								// nothing happened
							}
							mLastGroupFormed = arg0.groupFormed;
						} else {
							if (mLastGroupFormed) {
								listener.onInvokeConnectionEvent(
										EVENT_CONNECTIONBROKEN, arg0);
							}
						}
					}
				});
		// teamManager.onConnected();
	}

	private void onConnectionBreaken() {
		// log.i(TAG, "I'm Disconnected.");
		// teamManager.onDisconnected();
		listener.onInvokeConnectionEvent(EVENT_CONNECTIONBROKEN, null);
		// teamManager.onConnected();
	}

	/*
	 * 一般不用接口，因为不一定在主线程调用，通常采用handler发消息的方式
	 */
	public interface ConnectionManagerListener {
		public void onInvokeConnectionEvent(int eventId, Object obj);
	}

	private class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
		public WiFiDirectBroadcastReceiver() {
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
			intentFilter
					.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		}

		IntentFilter intentFilter = new IntentFilter();

		public IntentFilter getWifiDirectFilter() {
			return intentFilter;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				mWifiP2pEnabled = intent.getIntExtra(
						WifiP2pManager.EXTRA_WIFI_STATE,
						WifiP2pManager.WIFI_P2P_STATE_DISABLED) == WifiP2pManager.WIFI_P2P_STATE_ENABLED;
				handleP2pStateChanged();
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
					.equals(action)) {
				if (mWifiP2pManager == null)
					return;
				NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				WifiP2pInfo wifip2pinfo = (WifiP2pInfo) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
				if (mWifiP2pManager != null) {
					mWifiP2pManager.requestGroupInfo(mChannel,
							ConnectionManager.this);
				}
				mLastGroupFormed = wifip2pinfo.groupFormed;
				if (networkInfo.isConnected()) {
					onConnectionEstablished();
					log.i(TAG, "Connected");
				} else if (mLastGroupFormed != true) {
					// we are disconnected
					onConnectionBreaken();
					log.i(TAG, "Disconnected");
				}
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
					.equals(action)) {
				mThisDevice = (WifiP2pDevice) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				Log.d(TAG, "Update device info: " + mThisDevice);
				// device state changed
			} else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION
					.equals(action)) {
				int discoveryState = intent.getIntExtra(
						WifiP2pManager.EXTRA_DISCOVERY_STATE,
						WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
				log.i(TAG, "Discovery state changed: " + discoveryState);
				if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
					Toast.makeText(context, "discoverstarted",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "discoverend", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	}

	private class ErrorSolutionActionListener implements ActionListener {
		public static final int DISCOVERY = 1;
		public static final int ADDLOCALSERVICE = 2;
		public static final int REDISCOVERY = 3;
		public static final int THIRDDISCOVERY = 4;
		public static final int CLEARSERVICEREQUEST = 5;
		public static final int CONNECT = 6;
		public static final int CREATEGROUP = 7;
		public static final int OTHER = 100;
		private int action;
		private String actionDescription;

		public ErrorSolutionActionListener(int action) {
			this.action = action;
		}

		public ErrorSolutionActionListener(int action, String actionDescription) {
			this.action = action;
			this.actionDescription = actionDescription;
		}

		private String toActionString() {
			if (actionDescription != null)
				return actionDescription;
			switch (action) {
			case DISCOVERY:
				return "DISCOVERY";
			case ADDLOCALSERVICE:
				return "ADDLOCALSERVICE";
			case REDISCOVERY:
				return "REDISCOVERY";
			case THIRDDISCOVERY:
				return "THIRDDISCOVERY";
			case CLEARSERVICEREQUEST:
				return "CLEARSERVICEREQUEST";
			default:
				return "action";
			}
		}

		public void onFailure(int reason) {
			if (action == DISCOVERY
					&& reason == WifiP2pManager.NO_SERVICE_REQUESTS) {
				addServiceRequest();
				action = REDISCOVERY;
				actionDescription += " 2 ";
				mWifiP2pManager.discoverServices(mChannel, this);
			}
			if (action == REDISCOVERY
					&& reason == WifiP2pManager.NO_SERVICE_REQUESTS) {
				wifiStateManager.resetWifi();
				addServiceRequest();
				action = THIRDDISCOVERY;
				actionDescription += " 3 ";
				mWifiP2pManager.discoverServices(mChannel, this);
			} else {
				log.i(TAG, toActionString() + " WRONG:"
						+ WifiDirectConnectionUitl.errorStateToString(reason));
			}
		}

		public void onSuccess() {
			log.i(TAG, toActionString() + " OK");
		}

	}

	private void handleP2pStateChanged() {
		// TODO Auto-generated method stub
		log.i(TAG, "p2psearch state changed");
	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup arg0) {
		// TODO Auto-generated method stub
		if (arg0 != null) {
			String groupInfoStr = "";
			groupInfoStr += "Am I the group Owner: " + arg0.isGroupOwner()
					+ "\n";
			groupInfoStr += "ClientListNumber: " + arg0.getClientList().size()
					+ "\n";
			groupInfoStr += "Passphrase: " + arg0.getPassphrase() + "\n";
			log.i(groupInfoStr);
		}
	}

}