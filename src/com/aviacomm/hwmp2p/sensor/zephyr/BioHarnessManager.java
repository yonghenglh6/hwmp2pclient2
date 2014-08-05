package com.aviacomm.hwmp2p.sensor.zephyr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.aviacomm.hwmp2p.HWMP2PClient;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ZephyrProtocol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class BioHarnessManager {
	BluetoothAdapter adapter = null;
	BTClient _bt;
	ZephyrProtocol _protocol;
	NewConnectedListener _NConnListener;

	private final static String TAG = "bluetooth";
	private Handler handler;
	public BioHarnessManager(Handler handler, Context context) {
		this.handler=handler;
		/*
		 * Sending a message to android that we are going to initiate a pairing
		 * request
		 */
		IntentFilter filter = new IntentFilter(
				"android.bluetooth.device.action.PAIRING_REQUEST");
		/*
		 * Registering a new BTBroadcast receiver from the Main Activity context
		 * with pairing request event
		 */
		context.registerReceiver(new BTBroadcastReceiver(), filter);
		// Registering the BTBondReceiver in the application that the status of
		// the receiver has changed to Paired
		IntentFilter filter2 = new IntentFilter(
				"android.bluetooth.device.action.BOND_STATE_CHANGED");
		context.registerReceiver(new BTBondReceiver(), filter2);
	}

	private class BTBondReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			BluetoothDevice device = adapter.getRemoteDevice(b.get(
					"android.bluetooth.device.extra.DEVICE").toString());
			Log.d("Bond state", "BOND_STATED = " + device.getBondState());
		}
	}
	public void connect(){
		String BhMacID = "00:07:80:9D:8A:E8";
		// String BhMacID = "00:07:80:88:F6:BF";
		adapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = adapter
				.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().startsWith("BH")) {
					BluetoothDevice btDevice = device;
					BhMacID = btDevice.getAddress();
					Log.i(TAG, "BhMacID :" + BhMacID);
					break;

				}
			}
		}

		// BhMacID = btDevice.getAddress();
		BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
		String DeviceName = Device.getName();
		_bt = new BTClient(adapter, BhMacID);
		_NConnListener = new NewConnectedListener(handler,
				handler);
		_bt.addConnectedEventListener(_NConnListener);
		if (_bt.IsConnected()) {
			_bt.start();
			HWMP2PClient.log.i("Connected To bt");
			// Reset all the values to 0s

		} else {
			HWMP2PClient.log.i("Cant connected To bt");
		}
	}
	public void disconnect(){
		/*
		 * This disconnects listener from acting on received
		 * messages
		 */
		_bt.removeConnectedEventListener(_NConnListener);
		/*
		 * Close the communication with the device & throw an
		 * exception if failure
		 */
		_bt.Close();
	}
	private class BTBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BTIntent", intent.getAction());
			Bundle b = intent.getExtras();
			Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE")
					.toString());
			Log.d("BTIntent",
					b.get("android.bluetooth.device.extra.PAIRING_VARIANT")
							.toString());
			try {
				BluetoothDevice device = adapter.getRemoteDevice(b.get(
						"android.bluetooth.device.extra.DEVICE").toString());
				Method m = BluetoothDevice.class.getMethod("convertPinToBytes",
						new Class[] { String.class });
				byte[] pin = (byte[]) m.invoke(device, "1234");
				m = device.getClass().getMethod("setPin",
						new Class[] { pin.getClass() });
				Object result = m.invoke(device, pin);
				Log.d("BTTest", result.toString());
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
