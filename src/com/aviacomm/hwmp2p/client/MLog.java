package com.aviacomm.hwmp2p.client;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * show the information to the State Panel. 
 */
public class MLog {
	// TextView statusText;
	Handler handler;

	public MLog(Handler handler) {
		this.handler = handler;
	}

	public void i(String tag, String msg) {
		Log.i(tag, msg);
		pushMessage(tag + ":" + msg + "\n", Color.WHITE);
	}

	public void i(String msg) {
		Log.i("default", msg);
		pushMessage(msg + "\n", Color.WHITE);
	}

	public void r(String msg) {
		Log.i("default", msg);
		pushMessage("====="+msg + "\n", Color.RED);
	}

	private void pushMessage(String message, int color) {
		Bundle bundle = new Bundle();
		bundle.putString("message", message);
		bundle.putInt("color", color);
		Message msg = handler.obtainMessage(MessageEnum.LOGMESSAGE);
		msg.setData(bundle);
		msg.sendToTarget();
	}
}
