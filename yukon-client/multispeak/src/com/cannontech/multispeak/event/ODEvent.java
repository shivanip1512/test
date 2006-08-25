/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.OutageDetectionEvent;

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
	public ODEvent(MultispeakVendor mspVendor_, long pilMessageID_)//, int meterCount_)
	{
		super(mspVendor_, pilMessageID_);
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
