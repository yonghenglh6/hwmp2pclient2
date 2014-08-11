package com.aviacomm.hwmp2p.client.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.aviacomm.hwmp2p.connection.InetMessage;
import com.aviacomm.hwmp2p.connection.TeamClientList;
import com.aviacomm.hwmp2p.connection.TeamClientListItem;
import com.aviacomm.hwmp2p.connection.TeamControl;
import com.aviacomm.hwmp2p.ui.ActionPageFragment;
import com.aviacomm.hwmp2p.ui.DisplayPageFragment;
import com.aviacomm.hwmp2p.util.ClientStateMessage;
import com.aviacomm.hwmp2p.util.CommonSingleCircleThread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ClientConnection {
	int port;

	public static final int WAHT_CLIENTSTATE = 53693;
	TeamClientList teamClientList;

	public ClientConnection() {
	}

	public void init(int port) {
		this.port = port;
		listChanged();
	}

	ServerSocket serverSocket;
	boolean isFoundCommander = false;

	public void listChanged() {
		Log.i("net", "listchanged" + teamClientList.getData().length);
		if (isFoundCommander == true)
			return;
		Object[] items = (Object[]) teamClientList.getData();
		for (int i = 0; i < items.length; i++) {
			TeamClientListItem tmpItem = (TeamClientListItem) items[i];
			if (tmpItem.rule.equals("commander")) {
				Log.i("net", "commander found");

				connectToCommander(tmpItem);
				isFoundCommander = true;
				break;
			}
		}
	}

	Socket socketToCommander;
	InformCommanderThread informCommanderThread;

	public void connectToCommander(TeamClientListItem item) {
		informCommanderThread = new InformCommanderThread(item.address,
				item.listenPort);
		informCommanderThread.start();
	}

	public void setTeamClientList(TeamClientList teamClientList) {
		this.teamClientList = teamClientList;
	}

	public void clientListChanged() {
		listChanged();
	}

	public class InformCommanderThread extends CommonSingleCircleThread {
		Socket csocket;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		InetAddress addrss;
		int port;

		public InformCommanderThread(InetAddress addrss, int port) {
			// this.csocket = socket;
			this.addrss = addrss;
			this.port = port;
		}

		public void collectInfo(ClientStateMessage clientStateMessage) {
			clientStateMessage.i_heart = DisplayPageFragment.i_heart;
			clientStateMessage.i_breath = DisplayPageFragment.i_breath;
			clientStateMessage.i_temperature = DisplayPageFragment.i_temperature;
			clientStateMessage.isDanger = ActionPageFragment.urgent;
		}

		public void setUp() {
			this.setInterval(500);
			try {
				csocket = new Socket(addrss, port);
				oos = new ObjectOutputStream(csocket.getOutputStream());
				ois = new ObjectInputStream(csocket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void tearDown() {
			try {
				if (oos != null)
					oos.close();
				if (ois != null)
					ois.close();
				if (csocket != null)
					csocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// towork
		}

		public void oneTask() {
			ClientStateMessage amessage = new ClientStateMessage();
			collectInfo(amessage);
			InetMessage inetMessage = new InetMessage();
			inetMessage.what = WAHT_CLIENTSTATE;
			inetMessage.obj = amessage;
	//		Log.i("net", "send a packet");
			try {
				oos.writeObject(inetMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if (oos != null)
					try {
						oos.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				e.printStackTrace();
			}
		}
	}
}
