package com.aviacomm.hwmp2p.team;

import android.net.wifi.p2p.WifiP2pDevice;


public  class MWifiDirectAP {
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