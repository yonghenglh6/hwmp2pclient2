package com.aviacomm.hwmp2p.client;

/*
 * store the protocol for message's "what" attribute.
 */
public class MessageEnum {
	public final static int BASE =  398301;
	public final static int LOGMESSAGE=             BASE + 1;
	public final static int BATTERYCHANGE =         BASE + 2;
	public final static int VOLUMECHANGE  =         BASE + 3;
	public final static int ORIENTATIONCHANGE =     BASE + 4;
	public final static int WIFIINTENSITYCHANGE =   BASE + 5;
	public final static int WIFIAPDISCOVED =        BASE + 6;
	public final static int CONNECTIONESTABLISHED = BASE + 7;
	public final static int CONNECTIONBROKEN      = BASE + 8;
	public final static int LOWBATTERYWARN      =   BASE + 9;
	public final static int CRITICALDANGERWARN    = BASE + 10;
	public final static int OUTOFRANGEWARN  =       BASE + 11;
}
