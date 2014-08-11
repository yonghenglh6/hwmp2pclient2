package com.aviacomm.hwmp2p.util;

import java.io.Serializable;



public class ClientStateMessage implements Serializable{
	private static final long serialVersionUID = -8776021681013534104L;
	public int i_heart;
	public float i_breath;
	public float i_temperature;
	public int isDanger;
}
