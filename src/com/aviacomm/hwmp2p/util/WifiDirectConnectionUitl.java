package com.aviacomm.hwmp2p.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

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

	public static int getAvailablePort(int startPort) {
		// TODO generate Local Port

		ServerSocket s = null;
		int i;
		boolean isFound = false;
		for (i = startPort; i < startPort + 1000; i++) {
			try {
				s = new ServerSocket(i); // IP 要扫描的
				isFound = true;
			} catch (UnknownHostException ex) {
				// Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE,
				// null, ex);
			} catch (IOException ex) {
				// Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE,
				// null, ex);
			} finally {
				try {
					if (s != null)
						s.close();
				} catch (IOException ex) {
					// Logger.getLogger(NewClass.class.getName()).log(Level.SEVERE,
					// null, ex);
				}
			}
			if (isFound) {
//				Log.i("default", "getAvailablePort" + i);
				return i;
			}

		}
		return -1;
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
