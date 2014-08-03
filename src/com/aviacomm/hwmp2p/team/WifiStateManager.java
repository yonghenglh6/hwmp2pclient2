package com.aviacomm.hwmp2p.team;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiStateManager {
	Context context;
	WifiManager wifiManager;

	public WifiStateManager(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
	}

	public void turnOnWifi() {
		if (wifiManager != null) {
			if (!wifiManager.isWifiEnabled())
				wifiManager.setWifiEnabled(true);
		}
	}

	public void turnOffWifi() {
		if (wifiManager != null) {
			if (wifiManager.isWifiEnabled())
				wifiManager.setWifiEnabled(false);
		}
	}

	public boolean isWifiOn() {
		if (wifiManager != null) {
			return wifiManager.isWifiEnabled();
		}
		return false;
	}

	public void resetWifi() {
		turnOffWifi();
		turnOnWifi();
	}

}
