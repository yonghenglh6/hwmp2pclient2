package com.aviacomm.hwmp2p.connection;

import java.io.Serializable;

public class InetMessage implements Serializable {
	private static final long serialVersionUID = 7239589273770682052L;
	public int what;
	public String arg1;
	public int arg2;
	public Object obj;	
}
