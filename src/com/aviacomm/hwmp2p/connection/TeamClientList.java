package com.aviacomm.hwmp2p.connection;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.util.Log;

public class TeamClientList {
	// use set to unique every item.
	List<TeamClientListItem> list;
	public static final int BASE = 514691;
	public static final int CLIENTADD = BASE + 1;
	public static final int CLIENTCLEAR = BASE + 2;
	public final String TAG="TeamClientList";
	Handler handler;
	public TeamClientList(Handler handler) {
		this.handler = handler;
		list = new ArrayList<TeamClientListItem>();
	}

	public void add(InetAddress address, String device,int listenPort,String rule) {
		TeamClientListItem item = new TeamClientListItem(address, device,listenPort,rule);
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).device.equals(item.device)) {
				list.get(i).address = item.address;
				return;
			}
		list.add(item);
		handler.obtainMessage(CLIENTADD, address.getHostAddress())
				.sendToTarget();
		Log.i(TAG, "clientList is "+list.size());
		Log.i(TAG, "newClient is"+address.getHostAddress());
		// TeamControl.listadapter.add(address.getHostAddress());
	}

	public void clear() {
		list.clear();
		handler.obtainMessage(CLIENTCLEAR).sendToTarget();
		// TeamControl.listadapter.clear();
	}

	public Object[] getData() {
		return list.toArray();
	}

	public String getItem(int position) {
		return list.get(position).address.getHostAddress();
	}

	public void setData(Object[] alist) {
		this.list.clear();

		handler.obtainMessage(CLIENTCLEAR).sendToTarget();
		// TeamControl.listadapter.clear();
		for (int i = 0; i < alist.length; i++) {
			TeamClientListItem tt = (TeamClientListItem) alist[i];
			list.add(tt);
			handler.obtainMessage(CLIENTADD, tt.address.getHostAddress())
					.sendToTarget();
			// TeamControl.listadapter.add(tt.address.getHostAddress());
		}
	}
}
