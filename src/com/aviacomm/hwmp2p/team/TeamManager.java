package com.aviacomm.hwmp2p.team;

import com.aviacomm.hwmp2p.team.ConnectionManager.MWifiDirectAP;

/*
 * This is for virtual team,which is designed for the Reconnection;
 */
public class TeamManager {
	public GROUPSTATE groupstate = GROUPSTATE.DISSOCIATE;
	public MWifiDirectAP currentap;

	enum GROUPSTATE {
		DISSOCIATE, JOINED, LEADER, BEOKEN
	}

	String currentGsignal;

	public void onStartConnect(MWifiDirectAP ap) {
		currentap = ap;
	}

	public void onConnected() {
		// TODO
		groupstate = GROUPSTATE.JOINED;
	}

	public void onDisconnected() {
		// TODO
		if (groupstate == GROUPSTATE.DISSOCIATE)
			return;
		groupstate = GROUPSTATE.BEOKEN;
	}

	public MWifiDirectAP getcurrentAP() {
		return currentap;
	}

	public boolean isBroken() {
		return groupstate == GROUPSTATE.BEOKEN;
	}

	public boolean isReformedAp(String anotherGsignal) {
		return groupstate == GROUPSTATE.BEOKEN
				&& anotherGsignal.equals(currentGsignal);
	}
}
