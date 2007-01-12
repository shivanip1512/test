/**
 * Copyright (C) 2005 - 2006 Sensus MS, all rights reserved.
 */

package com.amdswireless.messages.rx;

import java.io.Serializable;

/**
 * @author johng
 * @since: 12/17/2005
 * 
 * @version: AppMessageType6.java v 1.1  12/15/2006  xumingc
 */

/* Andorian Data Message, App Code 6
 * These messages store GPS and movement data
 * Obviously, most meters don't move, but they
 * are programmed with their lat/long when they
 * are installed
 */

public class AppMessageType6 extends DataMessage implements AppMessage, Serializable {
	private transient static final long serialVersionUID = 1L;
	
	private final String msgClass = "POSITION";
	
	private double latitude;
	private double longitude;
	private double speed;
	private double heading;
	private double altitude;

	/**
	 *  Default constructor for encoding.
	 */
	public AppMessageType6() {		
		super();
		super.setAppCode(6);
		super.setMessageType(6);	
	}
	
	/**
	 *  Default constructor for decoding. Extract information from Andorian message.
	 *  @param msg
	 */
	public AppMessageType6( char[] msg ) {
		super(msg);
		super.setMessageType(6);
		
		//int b1=msg[3];
		//int b2=msg[4];
		//int b3=msg[5];
		//b1 = b1*256; 
		//b2=b2*65536;
		//this.latitude=(b3+b2+b1)*90/8388608;
		this.latitude=( (int)((msg[3] << 16)  + (msg[4] << 8) + msg[5] ) ) *90/8388608.0;
		//b1=msg[6];
		//b2=msg[7];
		//b3=msg[8];
		//b1 = b1*256; 
		//b2=b2*65536;
		//this.longitude=(b3+b2+b1)*180/8388608;
		this.longitude=( (int)((msg[6] << 16) + (msg[7] << 8) + msg[8] ) ) *180/8388608.0;
		
		this.speed = (msg[9] * 256 + msg[10]) * 0.01;
		this.heading = (msg[11] * 256 + msg[12]) * 0.01;
		this.altitude = (msg[13] * 256 + msg[14]) * 0.1;
	}

	/**
	 * @return Returns the latitude.
	 */
	public double getLatitude() {
		if ( latitude > 90.0 ) {
			return this.latitude - 90.0;
		} 
		else {
			return this.latitude;
		}
	}
	
	/**
	 * @return Returns the longitude.
	 */
	public double getLongitude() {
		if ( this.longitude > 180 ) {
			return this.longitude-180;
		} 
		else {
			return this.longitude;
		}
	}
	
	/**
	 * @return Returns the speed.
	 */
	public double getSpeed() {
		return this.speed;
	}
	
	/**
	 * @return Returns the heading.
	 */
	public double getHeading() {
		return this.heading;
	}
	
	/**
	 * @return Returns the extendedResolution.
	 */
	public double altitude() {
		return this.altitude;
	}
	
	/**
	 * @return Returns the msgClass.
	 */
	public String getMsgClass() {
		return this.msgClass;
	}
	
	/**
	 * Set alarmTemperature.
	 * @param alarmTemperature
	 */
	public void setLatitude(double latitude) {		
		this.latitude = latitude;
	
		latitude = latitude * 8388608.0 / 90.0;		
		message[3] = (char) ( (((int) latitude) >> 16) &0xFF );
		message[4] = (char) ( (((int) latitude) >> 8) &0xFF );
		message[5] = (char) ( ((int) latitude) & 0xFF);		
	}
	
	/**
	 * Set alarmTemperature.
	 * @param alarmTemperature
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
		
		longitude = longitude * 8388608.0 / 180.0;		
		message[6] = (char) ( (((int) longitude) >> 16) &0xFF );
		message[7] = (char) ( (((int) longitude) >> 8) &0xFF );
		message[8] = (char) ( ((int) longitude) & 0xFF);
	}
	
	/**
	 * Set alarmTemperature.
	 * @param alarmTemperature
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
		
		speed = speed / 0.01;
		message[9] = (char) ( (((int) speed) >> 8) &0xFF );		
		message[10] = (char) ( ((int) speed) & 0xFF);
	}
	
	/**
	 * Set alarmTemperature.
	 * @param alarmTemperature
	 */
	public void setHeading(double heading) {
		this.heading = heading;
		
		heading = heading / 0.01;
		message[11] = (char) ( (((int) heading) >> 8) &0xFF );		
		message[12] = (char) ( ((int) heading) & 0xFF);
	}
	
	/**
	 * Set alarmTemperature.
	 * @param alarmTemperature
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
		
		altitude = altitude / 0.1;
		message[13] = (char) ( (((int) altitude) >> 8) &0xFF );		
		message[14] = (char) ( ((int) altitude) & 0xFF);
	}

}
