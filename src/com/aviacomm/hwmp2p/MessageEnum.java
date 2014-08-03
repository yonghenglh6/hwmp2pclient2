package com.aviacomm.hwmp2p;

/*
 * store the protocol for message's "what" attribute.
 */
public class MessageEnum {
	public final static int BASE =  98301;
	public final static int LOGMESSAGE=            BASE + 1;
	public final static int BATTERYCHANGE =        BASE + 2;
	public final static int VOLUMECHANGE  =        BASE + 3;
	public final static int ORIENTATIONCHANGE =    BASE + 4;
	public final static int WIFIINTENSITYCHANGE =  BASE + 5;
	public final static int WIFIAPDISCOVED =       BASE + 6;
}
