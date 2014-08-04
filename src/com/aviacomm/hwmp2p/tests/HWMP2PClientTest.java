package com.aviacomm.hwmp2p.tests;

import junit.framework.Assert;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aviacomm.hwmp2p.HWMP2PClient;
import com.aviacomm.hwmp2p.R;
import com.aviacomm.hwmp2p.sensor.MusicVolumeManager;
import com.aviacomm.hwmp2p.team.ConnectionManager;
import com.aviacomm.hwmp2p.team.WifiStateManager;
import com.aviacomm.hwmp2p.uitl.DevelopUtil;
import com.aviacomm.hwmp2p.uitl.DevelopUtil.waitCondition;

public class HWMP2PClientTest extends
		ActivityInstrumentationTestCase2<HWMP2PClient> {
	private WifiStateManager wifiStateManager;
	private HWMP2PClient mainTestActivity;
	private Button resetWifi;
	private RadioGroup radioGroup;
	View decorView;

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
		decorView = mainTestActivity.getWindow().getDecorView();
	}

	public boolean shouldWeResetWIFI = true;

	public void testBatteryIcon() {
		ProgressBar bar = (ProgressBar) mainTestActivity
				.findViewById(R.id.batteryProgressBar);
		ViewAsserts.assertOnScreen(decorView, bar);
	}

	public void testVolume() {
		ProgressBar vol = (ProgressBar) mainTestActivity
				.findViewById(R.id.volumeProgressBar);
		Button inc = (Button) mainTestActivity
				.findViewById(R.id.mainpage_incvolume);
		Button dec = (Button) mainTestActivity
				.findViewById(R.id.mainpage_decvolume);
		ViewAsserts.assertOnScreen(decorView, vol);
		ViewAsserts.assertOnScreen(decorView, inc);
		ViewAsserts.assertOnScreen(decorView, dec);
		MusicVolumeManager manager = new MusicVolumeManager(mainTestActivity);
		int curpro = vol.getProgress();
		assertTrue(manager.getVolumeLevel() == curpro);
		TouchUtils.clickView(this, inc);
		int chgpro = vol.getProgress();
		assertTrue(chgpro > curpro);
		assertTrue(manager.getVolumeLevel() == chgpro);
		curpro = chgpro;
		TouchUtils.clickView(this, dec);
		chgpro = vol.getProgress();
		assertTrue(chgpro < curpro);
		assertTrue(manager.getVolumeLevel() == chgpro);
	}

	public void testCompass() {
		ImageView compass = (ImageView) mainTestActivity
				.findViewById(R.id.compass_pointer);
		assertNotNull(compass);
		ViewAsserts.assertOnScreen(decorView, compass);
		// Other tests about compass needs mock object, which we will do in the
		// future. For now, please check the accuracy manually;
	}

	@LargeTest
	public void testClientConnection() {
		if (shouldWeResetWIFI)
			TouchUtils.clickView(this, resetWifi);
		assertTrue(wifiStateManager.isWifiOn());
		Button scan = (Button) mainTestActivity
				.findViewById(R.id.mainpage_scan);
		;
		TouchUtils.clickView(this, scan);
		SystemClock.sleep(200);
		radioGroup = (RadioGroup) mainTestActivity.findViewById(R.id.apGroup);
		assertNotNull(radioGroup);
		ViewAsserts.assertOnScreen(decorView, radioGroup);
		DevelopUtil.waitAndCheckUntil(new waitCondition() {
			public boolean task() {
				return radioGroup.getChildCount() != 0;
			}
		});
		assertTrue("Find no AP,Please Check", radioGroup.getChildCount() > 0);
		RadioButton button = (RadioButton) radioGroup.getChildAt(0);
		assertNotNull(button);
		TouchUtils.clickView(this, button);
		Button connect = (Button) mainTestActivity
				.findViewById(R.id.apselector_connect);
		TouchUtils.clickView(this, connect);

		final ConnectionManager connectionManager = mainTestActivity
				.getConnectionManager();
		DevelopUtil.waitAndCheckUntil(new waitCondition() {
			@Override
			public boolean task() {
				return connectionManager.isConnected();
			}
		});
		assertTrue("Found service but connected Failed!",
				connectionManager.isConnected());
		SystemClock.sleep(200);
		ImageView wifiIndication = (ImageView) mainTestActivity
				.findViewById(R.id.mainpage_connection_establish_indicator);
		ViewAsserts.assertOnScreen(decorView, wifiIndication);
		assertTrue(wifiIndication.getAlpha() == 0);
	}
}
