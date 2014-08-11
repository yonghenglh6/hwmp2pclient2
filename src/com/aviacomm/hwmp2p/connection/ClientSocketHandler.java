package com.aviacomm.hwmp2p.connection;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

import com.aviacomm.hwmp2p.util.WifiDirectConnectionUitl;

public class ClientSocketHandler extends Thread {

	private static final String TAG = "ClientSocketHandler";
	private Handler handler;
	private InetAddress mAddress;
	TeamClientList clientlist;
	InputStream iStream;
	OutputStream oStream;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;

	public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress,
			TeamClientList clientlist) {
		this.handler = handler;
		this.mAddress = groupOwnerAddress;
		this.clientlist = clientlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		socket = new Socket();
		try {
			socket.bind(null);

			socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
					TeamControl.SERVER_CONTROL_PORT), 5000);
			Log.d(TAG, "Launching the I/O handler");
			// chat = new ChatManager(socket, handler);
			// new Thread(chat).start();
			iStream = socket.getInputStream();
			oStream = socket.getOutputStream();
			ois = new ObjectInputStream(iStream);
			oos = new ObjectOutputStream(oStream);
			introduceMyself();
			requestClientList();
			while (true) {
				handlerMessage();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeAllConnection();
		}
	}

	public void introduceMyself() {
		int port = WifiDirectConnectionUitl.getAvailablePort(TeamControl.APP_PORT);
		try {
			InetMessage outmessage = new InetMessage();
			outmessage.what = GroupOwnerSocketHandler.INTRODUCE_ME;
			outmessage.arg2 = port;
			outmessage.arg1 = TeamControl.rule;
			oos.writeObject(outmessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler.obtainMessage(TeamControl.WAHT_INTRODUCE_MYSELF, port,0).sendToTarget();
	}

	public void requestClientList() {

		try {
			InetMessage outmessage = new InetMessage();
			outmessage.what = GroupOwnerSocketHandler.REQUEST_CLIENTLIST;
			oos.writeObject(outmessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeAllConnection() {

		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
			if (iStream != null)
				iStream.close();
			if (oStream != null)
				oStream.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handlerMessage() throws OptionalDataException,
			ClassNotFoundException, IOException {
		InetMessage readmessage = (InetMessage) ois.readObject();
		Log.i(TeamControl.TAG, "getMessage" + readmessage.what);
		if (readmessage.what == GroupOwnerSocketHandler.RESPONSE_CLIENTLIST) {
			clientlist.setData((Object[]) readmessage.obj);
		}
	}

}
