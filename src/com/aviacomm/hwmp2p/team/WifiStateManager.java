package com.aviacomm.hwmp2p.team;



import com.aviacomm.hwmp2p.util.DevelopUtil;
import com.aviacomm.hwmp2p.util.DevelopUtil.waitCondition;

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
		if (wifiManager.isWifiEnabled())
			turnOffWifi();
		DevelopUtil.waitAndCheckUntil(new waitCondition() {
			@Override
			public boolean task() {
				return !wifiManager.isWifiEnabled();
			}
		});
		turnOnWifi();
		DevelopUtil.waitAndCheckUntil(new waitCondition() {
			@Override
			public boolean task() {
				return wifiManager.isWifiEnabled();
			}
		});
	}

}
