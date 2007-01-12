/**
 * Copyright (C) 2006 Sensus MS, all rights reserved.
 */

package com.amdswireless.messages.rx;
import java.io.Serializable;

/**
 * @author Xuming.Chen
 * @since: 11/09/2006
 * 
 * @version: AppMessageType16.java v 1.0  12/04/2006  xumingc
 */

/* 
 * This is a parser for message type 16 (App Code 22)  
 * The bit field looks like this:
 *  Byte	 Field
 *  ------------------------------------------------
 *  0-3		 Prefered Buddy Address
 *  4-5		 Group Address
 *  6(0:6)	 Alarm Temperature (bits 0-6 of byte 6)
 *  6(7)     Extended resolution (bits 7 of byte 6)
 *  ------------------------------------------------
 */

public class AppMessageType16 extends DataMessage implements AppMessage, Serializable {
	/**
	 * 
	 */
	private transient static final long serialVersionUID = -2877720172526362109L;
	private transient static final int offset = 31;
	
	private final String msgClass = "CONFIG";
	
	private int preferredBuddyAddress;
	private int groupAddress;
	private int alarmTemperature;
	
	private boolean extendedResolution;
	
	
	/**
	 *  Default constructor for encoding.
	 */
	public AppMessageType16() {		
		super();
		super.setAppCode(0x16);
		super.setMessageType(0x16);	
	}
 
	/**
	 *  Default constructor for decoding. Extract information from Andorian message.
	 *  @param msg
	 */
	
	public AppMessageType16( char[] msg ) {		
		super(msg);
		super.setMessageType(0x16);
		
        //Pass it with the entire Andorian message; offset the starting byte by "offset".					
		this.preferredBuddyAddress = (int) (msg[0 + offset] + msg[1 + offset] << 8 
                                     + msg[2 + offset] << 16 + msg[3 + offset] << 24 );		
		this.groupAddress = (int) (msg[4 + offset] + msg[5 + offset] << 8);		
		this.alarmTemperature =(int) (msg[6 + offset] & 0x7F);	
		//Mask extendedResolution with 0x80, in case a higher bit gets set; one bit - set to be a boolean
		this.extendedResolution = ((msg[6 + offset] & 0x80) == 0x80);		
	}
	
	/**
	 * Set preferredBuddyAddress.
	 * @param preferredBuddyAddress
	 */
	public void setPreferredBuddyAddress(int preferredBuddyAddress) {		
		this.preferredBuddyAddress = preferredBuddyAddress;	
		message[0 + offset] = (char) (preferredBuddyAddress & 0xFF);
		message[1 + offset] = (char) ((preferredBuddyAddress & 0xFF00) >>> 8);
		message[2 + offset] = (char) ((preferredBuddyAddress & 0xFF0000) >>> 16);
		message[3 + offset] = (char) ((preferredBuddyAddress & 0xFF000000) >>> 24);
	}

	/**
	 * Set groupAddress.
	 * @param groupAddress
	 */
	public void setGroupAddress(int groupAddress) {		
		this.groupAddress = groupAddress;
		message[4 + offset] = (char) (groupAddress & 0xFF);
		message[5 + offset] = (char) ((groupAddress & 0xFF00) >>> 8);
	}
	
	/**
	 * Set alarmTemperature.
	 * @param alarmTemperature
	 */
	public void setAlarmTemperature(int alarmTemperature) {					
		this.alarmTemperature = alarmTemperature;
		message[6 + offset] |= (char) ((alarmTemperature & 0x7F) );
	}
	
	/**
	 * Set extendedResolution.
	 * @param extendedResolution
	 */
	public void setExtendedResolution(boolean extendedResolution) {		
		this.extendedResolution = extendedResolution;	
		if (extendedResolution)
			message[6 + offset] |= (char) 0x80;
	}
 
	/**
	 * @return Returns the preferredBuddyAddress.
	 */
	public int getPreferredBuddyAddress() {
		return preferredBuddyAddress;
	}
	
	/**
	 * @return Returns the groupAddress.
	 */
	public int getGroupAddress() {
		return groupAddress;
	}
	
	/**
	 * @return Returns the alarmTemperature.
	 */
	public int getAlarmTemperature() {
		return alarmTemperature;
	}
	
	/**
	 * @return Returns the extendedResolution.
	 */
	public boolean getExtendedResolution() {
		return extendedResolution;
	}
 
	/**
	 * @return Returns the msgClass.
	 */
	public String getMsgClass() {
		return msgClass;
	}
}