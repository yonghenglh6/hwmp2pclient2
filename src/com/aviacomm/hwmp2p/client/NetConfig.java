package com.aviacomm.hwmp2p.client;

import com.aviacomm.hwmp2p.util.WifiDirectConnectionUitl;

public class NetConfig {
	public static int ApPort;
	public static int ListenPort;
	
	public NetConfig(){
		ApPort=WifiDirectConnectionUitl.getAvailablePort(7584);
		ListenPort=WifiDirectConnectionUitl.getAvailablePort(8768);
	}
}
