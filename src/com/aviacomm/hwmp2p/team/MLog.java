package com.aviacomm.hwmp2p.team;

import android.os.Handler;
import android.util.Log;
/*
 * show the information to the State Panel. 
 */
public class MLog {
//	TextView statusText;
	Handler handler;
	public static final int LOGMESSAGE=6538643;
	public MLog(Handler handler){
		this.handler=handler;
	}
	public void i(String tag,String msg){
		Log.i(tag, msg);
		handler.obtainMessage(LOGMESSAGE,tag+":"+msg+"\n").sendToTarget();
	}
	public void i(String msg){
		Log.i("default", msg);
		handler.obtainMessage(LOGMESSAGE,msg+"\n").sendToTarget();
	}
}
