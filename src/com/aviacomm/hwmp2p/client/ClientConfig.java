package com.aviacomm.hwmp2p.client;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ClientConfig {
	public static final int CELSIUS = 1;
	public static final int FAHRENHEIT = 2;

	public static String DeviceName = null;
	public static String ID = null;
	public static int MinBreathRate = 0;
	public static int MaxBreathRate = 30;
	public static int MinHeartRate = 40;
	public static int MaxHeartRate = 90;
	public static int MaxBodyTemperature = 100;
	public static int TemperatureUnit = CELSIUS;
	Context context;
	SharedPreferences sharedPreferences;
	Map<String, String> defaultValue = new HashMap<String, String>();

	// String defaultAttr[][] = { { "devicename", "" }, { "deviceid", "" },
	// { "minheart", "40" }, { "maxheart", "160" }, { "minbreath", "5" },
	// { "maxbreath", "40" }, { "temperature", "37" } };

	// String attrname[] = { "devicename", "deviceid", "minheart", "maxheart",
	// "minbreath", "maxbreath", "temperature" };

	// String attrDefaultValue[] = { "NOTSET", "0", "40", "160", "5", "60", "37"
	// };

	public ClientConfig(Context context) {
		this.context = context;
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		initDefaultValue();
	}

	public void initDefaultValue() {
		// defaultValue.put("devicename", "");
		// defaultValue.put("deviceid", "");
		defaultValue.put("minheart", "40");
		defaultValue.put("maxheart", "160");
		defaultValue.put("minbreath", "5");
		defaultValue.put("maxbreath", "40");
		defaultValue.put("temperature", "37");
	}

	public void setValue(Map<String, String> map) {

	}

	public void setValue(String key, String value) {
		SharedPreferences.Editor sharedata = sharedPreferences.edit();
		sharedata.putString(key, value);
		sharedata.commit();
	}

	public String getValue(String key) {
		return sharedPreferences.getString(key,
				defaultValue.containsKey(key) ? defaultValue.get(key) : "");
	}
}
