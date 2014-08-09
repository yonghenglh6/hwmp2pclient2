package com.aviacomm.hwmp2p.team;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import com.aviacomm.hwmp2p.client.MessageEnum;
import com.aviacomm.hwmp2p.team.ConnectionManager.ConnectionManagerListener;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/*
 * This is for virtual team,which is designed for the Reconnection;
 */
public class TeamManager implements ConnectionManagerListener {
	public GROUPSTATE groupstate = GROUPSTATE.DISSOCIATE;
	public MWifiDirectAP currentap;
	ConnectionManager connectionManager;
	private static final String TAG = "TeamManager";
	private static final int BASE = 43659618;
	public static final int WHAT_WIFIAPDISCOVED = BASE + 1;
	public static final int WHAT_CONNECTIONESTABLISHED = BASE + 2;
	public static final int WHAT_CONNECTIONBROKEN = BASE + 3;

	enum GROUPSTATE {
		DISSOCIATE, JOINING, JOINED, LEADER, BEOKEN
	}

	Context context;
	String currentGsignal;
	Handler handler;

	public TeamManager(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		connectionManager = new ConnectionManager(context, this);
	}

	public void initial() {
		
		connectionManager.initial();
	}

	public boolean isConnected() {
		return connectionManager.isConnected();
	}

	public void discoverTeam() {
		connectionManager.discoverTeamService();
	}

	public void constantCreateTeam() {
		if (createTeamThread != null)
			createTeamThread.setIsCreateTeam(false);
		createTeamThread = new CreateTeamThread();
		createTeamThread.start();
	}

	public void createTeam() {
		connectionManager.createTeamService();
	}

	public void connect(MWifiDirectAP ap) {
		currentap=ap;
		connectionManager.connect(ap);
	}

	public MWifiDirectAP getcurrentAP() {
		return currentap;
	}

	@Override
	public void onInvokeConnectionEvent(int eventId, Object obj) {
		switch (eventId) {
		case ConnectionManager.EVENT_CONNECTIONBROKEN:
			handler.obtainMessage(MessageEnum.CONNECTIONBROKEN).sendToTarget();
			break;
		case ConnectionManager.EVENT_CONNECTIONESTABLISHED:
			handler.obtainMessage(MessageEnum.CONNECTIONESTABLISHED, currentap)
					.sendToTarget();
			break;
		case ConnectionManager.EVENT_WIFIAPDISCOVED:
			Log.i(TAG, "apfound");
			handler.obtainMessage(MessageEnum.WIFIAPDISCOVED, obj).sendToTarget();
			break;
		}
	}

	public CreateTeamThread createTeamThread;

	public class CreateTeamThread extends Thread {
		public boolean isCreateTeam = true;

		public void run() {
			while (isCreateTeam) {
				createTeam();
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		public void setIsCreateTeam(boolean istrue) {
			isCreateTeam = istrue;
		}
	}
}
