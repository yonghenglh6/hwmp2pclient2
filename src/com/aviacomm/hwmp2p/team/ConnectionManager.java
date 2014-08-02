package com.aviacomm.hwmp2p.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aviacomm.hwmp2p.HWMP2PClient;
import com.aviacomm.hwmp2p.MessageEnum;
import com.aviacomm.hwmp2p.uitl.WifiDirectConnectionUitl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
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

public class ConnectionManager implements GroupInfoListener {
	ConnectionManagerListener listener;
	public static boolean backupAp = false;
	private Channel mChannel;
	private WifiP2pManager mWifiP2pManager;
	private TeamManager teamManager;
	// public static final String TXTRECORD_PROP_AVAILABLE = "available";
	// public static final String SERVICE_INSTANCE = "hwmp2pclient";
	Activity mainActivity;
	WiFiDirectBroadcastReceiver receiver;
	private DnsSdServiceResponseListener dnsSdServiceResponseListener;
	private DnsSdTxtRecordListener dnsSdTxtRecordListener;
	public final String TAG = "ConnectionManager";
	public final String INSTANCENAME = "HWMP2P";
	public final String REGISTIONTYPE = "HWMP2P";
	Handler handler;
	List<MWifiDirectAP> aplist;
	WifiP2pDnsSdServiceRequest serviceRequest;
	private boolean mWifiP2pEnabled;
	private boolean mWifiP2pSearching;
	private int mConnectedDevices;
	private WifiP2pGroup mConnectedGroup;
	private boolean mLastGroupFormed = false;
	private WifiP2pDevice mThisDevice;
	public int SERVER_PORT;
	public String nickname;

	public ConnectionManager(Activity activity,
			ConnectionManagerListener listener, Handler handler) {
		this.mainActivity = activity;
		this.listener = listener;
		this.handler = handler;
	}

	// ！！！！ you must invoke initial function before any usage.
	public void initial() {
		aplist = new ArrayList<ConnectionManager.MWifiDirectAP>();
		mWifiP2pManager = (WifiP2pManager) mainActivity
				.getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mWifiP2pManager.initialize(mainActivity,
				mainActivity.getMainLooper(), null);
		teamManager = new TeamManager();
		receiver = new WiFiDirectBroadcastReceiver();
		mainActivity.registerReceiver(receiver, receiver.getWifiDirectFilter());
		dnsSdServiceResponseListener = new DnsSdServiceResponseListener() {
			@Override
			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice srcDevice) {
				// A service has been discovered. Is this our app?
				Log.i(TAG, "ServiceInstance Found");
				if (instanceName.equalsIgnoreCase(INSTANCENAME)) {
					MWifiDirectAP ap = MWifiDirectAP.getInstance(srcDevice,
							registrationType);
					checkForList(ap);
				}
			}
		};

		dnsSdTxtRecordListener = new DnsSdTxtRecordListener() {
			@Override
			public void onDnsSdTxtRecordAvailable(String arg0,
					Map<String, String> record, WifiP2pDevice device) {
				Log.i(TAG, "TXT record Found");
				MWifiDirectAP ap = MWifiDirectAP.getInstance(device,
						record.get("action"), record.get("gsignal"),
						record.get("nickname"), record.get("listenport"));
				checkForList(ap);
			}
		};
		mWifiP2pManager.setDnsSdResponseListeners(mChannel,
				dnsSdServiceResponseListener, dnsSdTxtRecordListener);
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
			if (ori == null)
				aplist.add(ap);
			else {
				int state = ori.Combine(ap);
				if (state == 1) {
					// An ap should be listed!
					handler.obtainMessage(MessageEnum.WIFIAPDISCOVED, ori)
							.sendToTarget();
				}
			}
		}
	}

	public void connect(MWifiDirectAP ap) {

	}

	public boolean isConnected() {
		return true;
	}

	public void discoverTeamService() {

		if (serviceRequest != null)
			mWifiP2pManager.removeServiceRequest(mChannel, serviceRequest,
					new ActionListener() {
						@Override
						public void onSuccess() {
							Log.i(TAG, "removeRequest");
						}

						@Override
						public void onFailure(int arg0) {
							Log.i(TAG,
									"removeRequest Failed"
											+ WifiDirectConnectionUitl
													.transferWifiDeviceStatus(arg0));
						}
					});
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest,
				new ActionListener() {
					@Override
					public void onSuccess() {
						// Success!
						Log.i(TAG, "Add Service request OK");
					}

					@Override
					public void onFailure(int code) {
						// Command failed. Check for P2P_UNSUPPORTED, ERROR, or
						// BUSY
						Log.i(TAG,
								"Add Service request Wrong"
										+ WifiDirectConnectionUitl
												.transferWifiDeviceStatus(code));
					}
				});
		mWifiP2pManager.discoverServices(mChannel, new ActionListener() {
			@Override
			public void onSuccess() {
				// Success!
				Log.i(TAG, "StartDiscover ap OK");
			}

			@Override
			public void onFailure(int code) {
				// Command failed. Check for P2P_UNSUPPORTED, ERROR, or BUSY
				Log.i(TAG,
						"StartDiscover ap Wrong"
								+ WifiDirectConnectionUitl
										.transferWifiDeviceStatus(code));
			}
		});
	}

	public void createTeamService() {
		Map<String, String> record = new HashMap<String, String>();
		SERVER_PORT = WifiDirectConnectionUitl.getAvailablePort();
		record.put("action", "CREATETEAM");
		record.put("gsignal", WifiDirectConnectionUitl.generateGsignal());
		record.put("listenport", String.valueOf(SERVER_PORT));
		record.put(
				"nickname",
				nickname == null ? ("FireFighter" + (int) (Math.random() * 1000))
						: nickname);
		record.put("available", "visible");

		mWifiP2pManager.clearLocalServices(mChannel, new ActionListener() {
			@Override
			public void onSuccess() {
				Log.i(TAG, "removeok");
			}

			@Override
			public void onFailure(int error) {
				Log.i(TAG, "removewrong" + error);
			}
		});
		WifiP2pDnsSdServiceInfo addteamservice = WifiP2pDnsSdServiceInfo
				.newInstance(INSTANCENAME, REGISTIONTYPE, record);
		mWifiP2pManager.addLocalService(mChannel, addteamservice,
				new ActionListener() {
					@Override
					public void onSuccess() {
						Log.i(TAG, "Create TeamService OK");
					}

					@Override
					public void onFailure(int error) {
						Log.i(TAG,
								"Create TeamService Wrong:"
										+ WifiDirectConnectionUitl
												.transferWifiDeviceStatus(error));
					}
				});
	}

	private void onConnectionEstablished() {

	}

	private void onConnectionBreaken() {

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
					Log.d(TAG, "Connected");
				} else if (mLastGroupFormed != true) {
					// we are disconnected
					onConnectionBreaken();
					Log.d(TAG, "Disconnected");
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
				Log.d(TAG, "Discovery state changed: " + discoveryState);
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

	public static class MWifiDirectAP {
		public WifiP2pDevice device;
		public String action;
		public String gsignal;
		public String nickname;
		public String listenPort;
		public String registerType;
		private int infoCompletion;
		public static final int RECORDFOUND = 1;
		public static final int REGISTERTYPEFOUND = 2;

		public MWifiDirectAP(WifiP2pDevice device, String action,
				String gsignal, String nickname, String listenPort,
				String registerType, int infoCompletion) {
			super();
			this.device = device;
			this.action = action;
			this.gsignal = gsignal;
			this.nickname = nickname;
			this.listenPort = listenPort;
			this.registerType = registerType;
			this.infoCompletion = infoCompletion;
		}

		public static MWifiDirectAP getInstance(WifiP2pDevice device,
				String registerType) {
			return new MWifiDirectAP(device, null, null, null, null,
					registerType, REGISTERTYPEFOUND);
		}

		public static MWifiDirectAP getInstance(WifiP2pDevice device,
				String action, String gsignal, String nickname,
				String listenPort) {
			return new MWifiDirectAP(device, action, gsignal, nickname,
					listenPort, null, RECORDFOUND);
		}

		/*
		 * return 1 if become complete from incomplete update INFO from another
		 * return 0 return -1 if device address is not same
		 */
		public int Combine(MWifiDirectAP another) {
			int isComplete = 0;
			if (!device.deviceAddress.equals(another.device.deviceAddress))
				return -1;

			if (another.infoCompletion == RECORDFOUND) {
				action = another.action;
				gsignal = another.gsignal;
				nickname = another.nickname;
				listenPort = another.listenPort;
			} else if (another.infoCompletion == REGISTERTYPEFOUND) {
				registerType = another.registerType;
			}
			if (infoCompletion + another.infoCompletion == RECORDFOUND
					+ REGISTERTYPEFOUND) {
				isComplete = 1;
				infoCompletion = RECORDFOUND + REGISTERTYPEFOUND;
			}
			return isComplete;
		}
		/*
		 * @Override public boolean equals(Object o) { return
		 * device.deviceAddress .equalsIgnoreCase(((WifiP2pDevice)
		 * o).deviceAddress); }
		 */
	}

	private void handleP2pStateChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup arg0) {
		// TODO Auto-generated method stub

	}
}
