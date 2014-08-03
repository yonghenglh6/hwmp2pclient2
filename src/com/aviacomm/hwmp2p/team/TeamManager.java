package com.aviacomm.hwmp2p.team;

import com.aviacomm.hwmp2p.team.ConnectionManager.MWifiDirectAP;
/*
 * This is for virtual team;
 */
public class TeamManager {
	public GROUPSTATE groupstate=GROUPSTATE.DISSOCIATE;
	
	enum GROUPSTATE{
		DISSOCIATE,
		JOINED,
		LEADER,
		BEOKEN
	}
	String currentGsignal;
	
	
	public void onConnectToTeam(MWifiDirectAP ap){
		
	}
	public void onConnected(){
		
	}
	public boolean isBroken(){
		return groupstate==GROUPSTATE.BEOKEN;
	}
	
	public boolean isReformedAp(String anotherGsignal){
		return  groupstate==GROUPSTATE.BEOKEN&&anotherGsignal.equals(currentGsignal);
	}
}
