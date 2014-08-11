package com.aviacomm.hwmp2p.connection;

import java.io.Serializable;
import java.net.InetAddress;


public class TeamClientListItem implements Serializable {
	private static final long serialVersionUID = 6353252148882916642L;
	public InetAddress address;
	public String device;
	public int listenPort;
	public String rule;
	public TeamClientListItem(InetAddress address, String device,int listenPort,String rule) {
		super();
		this.address = address;
		this.device = device;
		this.listenPort=listenPort;
		this.rule=rule;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		TeamClientListItem b = (TeamClientListItem) o;
		return (address.getHostAddress().equals(b.address.getHostAddress()));
		// return super.equals(o);
	}
}
