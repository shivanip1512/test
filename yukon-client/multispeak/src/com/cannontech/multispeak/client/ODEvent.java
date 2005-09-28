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
public class ODEvent extends MultispeakEvent{

   private OutageDetectionEvent outageDetectionEvent = null;

	/**
	 * @param vendorName_
	 * @param pilMessageID_
	 */
	public ODEvent(String vendorName_, long pilMessageID_)//, int meterCount_)
	{
		super(vendorName_, pilMessageID_);
	}

	
	/**
	 * @return
	 */
	public OutageDetectionEvent getOutageDetectionEvent()
	{
		return outageDetectionEvent;
	}
	/**
	 * @param outageDetectionEvent_
	 */
	public void setOutageDetectionEvent(OutageDetectionEvent outageDetectionEvent_)
	{
		this.outageDetectionEvent = outageDetectionEvent_;
	}
}