/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

import com.cannontech.multispeak.OutageDetectionEvent;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ODEvent extends Observable{
	
	/** The unique messageID for all of the commands sent to pil */
	private long pilMessageID = -1;
	/** The total number of meters tested */
	private int totalMeterCount = 0;
	/** The number of meters processed, this count and totalMeterCount must be equal before Notification message will be sent */
	private int completedMeterCount = 0;
	/** A vector of OutageDetectionEvents, meterCount should equal the number of "expected" ODEvents elements */
	private Vector ODEvents = new Vector();
	
	/**
	 * 
	 */
	public ODEvent(long pilMessageID_, int meterCount_) {
		super();
		pilMessageID = pilMessageID_;
		totalMeterCount = meterCount_;
	}

	/**
	 * @return
	 */
	public int getTotalMeterCount() {
		return totalMeterCount;
	}


	/**
	 * @return
	 */
	public long getPilMessageID() {
		return pilMessageID;
	}

	/**
	 * @param i
	 */
	public void setTotalMeterCount(int i) {
		totalMeterCount = i;
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
	public Vector getODEvents() {
		return ODEvents;
	}

	/**
	 * @param map
	 */
	public void setODEvents(Vector ODEvents_) {
		ODEvents = ODEvents_;
	}
	/**
	 * @return
	 */
	public int getCompletedMeterCount() {
		return completedMeterCount;
	}

	/**
	 * @param i
	 */
	public void setCompletedMeterCount(int i) {
		completedMeterCount = i;
		if( completedMeterCount >= getTotalMeterCount() )
		{
			setChanged();
			notifyObservers();
		}
	}

}
