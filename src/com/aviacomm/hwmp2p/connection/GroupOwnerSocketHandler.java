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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.aviacomm.hwmp2p.util.WifiDirectConnectionUitl;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class GroupOwnerSocketHandler extends Thread {
	ServerSocket socket = null;
	private final int THREAD_COUNT = 10;
	private Handler handler;
	private static final String TAG = "GroupOwnerSocketHandler";
	TeamClientList clientlist;
	InetAddress myaddress;
	List<SingalControlRunable> clientsockets;

	public GroupOwnerSocketHandler(Handler handler, InetAddress myaddress,
			TeamClientList clientlist) throws IOException {
		this.myaddress = myaddress;
		this.handler = handler;
		this.clientlist = clientlist;
		try {
			socket = new ServerSocket(TeamControl.SERVER_CONTROL_PORT);
			clientsockets = new ArrayList<SingalControlRunable>();
			Log.d("GroupOwnerSocketHandler", "Socket Started");
		} catch (IOException e) {
			e.printStackTrace();
			pool.shutdownNow();
			throw e;
		}

	}

	/**
	 * A ThreadPool for client sockets.
	 */
	private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
			THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

	public static int REQUEST_CLIENTLIST = 10;
	public static int RESPONSE_CLIENTLIST = 11;
	public static int INTRODUCE_ME = 12;

	class SingalControlRunable implements Runnable {
		Socket socket;
		InputStream iStream;
		OutputStream oStream;
		byte[] buffer = new byte[1024];
		int bytes;

		public SingalControlRunable(Socket socket) {
			this.socket = socket;
			try {
				iStream = socket.getInputStream();
				oStream = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ObjectOutputStream oos;
		ObjectInputStream ois;
		boolean run = false;

		@Override
		public void run() {

			// TODO Auto-generated method stub
			try {
				oos = new ObjectOutputStream(oStream);
				ois = new ObjectInputStream(iStream);
				run = true;
				while (true) {
					handleMessage();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				run = false;
			}
		}

		public void sendMessage(InetMessage message) {
			try {
				oos.writeObject(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public boolean isAlive() {
			if (run && socket.isConnected())
				return true;
			return false;
		}

		private void handleMessage() throws OptionalDataException,
				ClassNotFoundException, IOException {
			InetMessage readmessage = (InetMessage) ois.readObject();
			if (readmessage.what == REQUEST_CLIENTLIST) {
				oos.writeObject(newClientListMessage());
			}
			if (readmessage.what == INTRODUCE_ME) {
				clientlist.add(socket.getInetAddress(), socket.getInetAddress()
						.getHostName(), readmessage.arg2, readmessage.arg1);
				InetMessage outmessage = newClientListMessage();
				informAll(outmessage);
			}
		}
	}

	@Override
	public void run() {
		int port = WifiDirectConnectionUitl.getAvailablePort(TeamControl.APP_PORT);
		clientlist.add(myaddress, myaddress.getHostName(), port,
				TeamControl.rule);
		handler.obtainMessage(TeamControl.WAHT_INTRODUCE_MYSELF, port, 0)
				.sendToTarget();
		while (true) {
			try {
				// A blocking operation. Initiate a ChatManager instance when
				// there is a new connection
				Socket msocket = socket.accept();
				SingalControlRunable singal = new SingalControlRunable(msocket);
				// clientlist.add(msocket.getInetAddress(),
				// msocket.getInetAddress().getHostName(),WifiDirectConnectionUitl.getAvailablePort());
				// InetMessage outmessage=newClientListMessage();
				// informAll(outmessage);

				clientsockets.add(singal);
				pool.execute(singal);

				Log.d(TAG, "Launching the I/O handler");

			} catch (IOException e) {
				try {
					if (socket != null && !socket.isClosed())
						socket.close();
				} catch (IOException ioe) {
				}
				e.printStackTrace();
				pool.shutdownNow();
				break;
			}
		}
	}

	public InetMessage newClientListMessage() {
		InetMessage outmessage = new InetMessage();
		outmessage.what = RESPONSE_CLIENTLIST;
		outmessage.obj = clientlist.getData();
		
		return outmessage;
	}

	public void informAll(InetMessage message) {
		Iterator<SingalControlRunable> it = clientsockets.iterator();
		for (; it.hasNext();) {
			SingalControlRunable runa = it.next();
			// Message outmessage=new Message();
			// outmessage.what=RESPONSE_CLIENTLIST;
			// outmessage.obj=clientlist;
			if (runa.isAlive())
				runa.sendMessage(message);
		}
	}

}
