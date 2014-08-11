package com.aviacomm.hwmp2p.connection;

import java.io.IOException;

import com.aviacomm.hwmp2p.team.TeamManager;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;
import android.util.Log;

public class TeamControl {
	private Handler handler;
	// private ArrayAdapter<String> listadapter;
	static int SERVER_CONTROL_PORT = TeamManager.BASELISTENPORT;
	static int APP_PORT = 4559;
	public static final String TAG = "teamcontrol";

	public final static int WAHT_INTRODUCE_MYSELF = 27583;

	public TeamControl() {

	}

	public static String rule;
	public TeamClientList clientList;

	public void init(WifiP2pInfo info, String listenPort, Handler handler,
			String rule) {
		// this.listadapter=listadapter;
		this.handler = handler;
		TeamControl.rule = rule;
		try {
			SERVER_CONTROL_PORT = Integer.valueOf(listenPort);
		} catch (Exception e) {

		}
		Log.i(TAG, "listenport" + SERVER_CONTROL_PORT);
		clientList = new TeamClientList(handler);
		Thread handlerth = null;
		if (info.isGroupOwner) {
			Log.d(TAG, "Connected as group owner");
			try {
				handlerth = new GroupOwnerSocketHandler(getHandler(),
						info.groupOwnerAddress, clientList);
				handlerth.start();
			} catch (IOException e) {
				Log.d(TAG,
						"Failed to create a server thread - " + e.getMessage());
				return;
			}
		} else {
			Log.d(TAG, "Connected as peer");
			handlerth = new ClientSocketHandler(getHandler(),
					info.groupOwnerAddress, clientList);
			handlerth.start();
		}
	}

	public void clearList() {
		clientList.clear();
	}

	public void selectClient(int position) {
		Log.i(TAG, "select " + position + ": " + clientList.getItem(position));
	}

	public TeamClientList getList() {
		return clientList;
	}

	public Handler getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}

}
