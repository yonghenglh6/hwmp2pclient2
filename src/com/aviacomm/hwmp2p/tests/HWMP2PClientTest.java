package com.aviacomm.hwmp2p.tests;

import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.Button;

import com.aviacomm.hwmp2p.HWMP2PClient;
import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.team.WifiStateManager;

public class HWMP2PClientTest extends
		ActivityInstrumentationTestCase2<HWMP2PClient> {
	private WifiStateManager wifiStateManager;
	private HWMP2PClient mainTestActivity;
	private Button resetWifi;

	public HWMP2PClientTest() {
		super(HWMP2PClient.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(true);
		mainTestActivity = getActivity();
		resetWifi = (Button) mainTestActivity
				.findViewById(R.id.mainpage_resetwifi);
		wifiStateManager = new WifiStateManager(mainTestActivity);

	}

	private void resetWifi() {
		if (wifiStateManager.isWifiOn()) {
			TouchUtils.clickView(this, resetWifi);
			while (wifiStateManager.isWifiOn()) {
				SystemClock.sleep(200);
			}

			assertFalse(wifiStateManager.isWifiOn());
		}
		TouchUtils.clickView(this, resetWifi);
		while (!wifiStateManager.isWifiOn()) {
			SystemClock.sleep(200);
		}
		assertTrue(wifiStateManager.isWifiOn());
	}

	@LargeTest
	public void testClientConnection() {

	}

}
