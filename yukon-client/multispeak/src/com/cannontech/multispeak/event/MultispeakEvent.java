/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;

import java.util.Observable;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakEvent extends Observable{
    
	/** The unique vendor name who initiated the event */
	private String vendorName = "";
	/** The unique messageID for all of the commands sent to pil */
	private long pilMessageID = -1;

	/**
	 * 
	 */
	public MultispeakEvent(String vendorName_, long pilMessageID_) {
		super();
		vendorName = vendorName_;
		pilMessageID = pilMessageID_;
	}

	/**
	 * @return
	 */
	public long getPilMessageID() {
		return pilMessageID;
	}

	/**
	 * @param long1
	 */
	public void setPilMessageID(long long1) {
		pilMessageID = long1;
	}

	/**
	 * @return
	 */
	public String getVendorName()
	{
		return vendorName;
	}

	/**
	 * @param string
	 */
	public void setVendorName(String string)
	{
		vendorName = string;
	}
}
