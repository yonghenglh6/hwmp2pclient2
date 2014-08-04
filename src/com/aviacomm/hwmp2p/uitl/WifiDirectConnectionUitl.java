package com.aviacomm.hwmp2p.uitl;

import java.util.Random;


import android.net.wifi.p2p.WifiP2pManager;

/*
 * Some tools
 */
public class WifiDirectConnectionUitl {
	public static final int SERVERPORT = 5198;

	public static String errorStateToString(int status) {
		if (status == WifiP2pManager.BUSY)
			return "BUSY";
		if (status == WifiP2pManager.ERROR)
			return "ERROR";
		if (status == WifiP2pManager.P2P_UNSUPPORTED)
			return "P2P_UNSUPPORTED";
		if (status == WifiP2pManager.NO_SERVICE_REQUESTS)
			return "NO_SERVICE_REQUESTS";
		return "NOT KNOW," + status;
	}

	public static int getAvailablePort() {
		// TODO generate Local Port
		return SERVERPORT;
	}

	public static String generateGsignal() {
		String gsianal = "";
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			gsianal += random.nextInt() % 10;
		}
		return gsianal;
	}
}
