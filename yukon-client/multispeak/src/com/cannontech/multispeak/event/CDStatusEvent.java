/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import com.cannontech.multispeak.client.MultispeakVendor;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDStatusEvent extends CDEvent{

    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public CDStatusEvent(MultispeakVendor mspVendor_, long pilMessageID_, int returnMessages_,
            String transactionID_) {
        super(mspVendor_, pilMessageID_, returnMessages_, transactionID_);
    }
    
	/**
	 * @param mspVendor_
	 * @param pilMessageID_
	 */
	public CDStatusEvent(MultispeakVendor mspVendor_, long pilMessageID_, String transactionID_) {
		this(mspVendor_, pilMessageID_, 1, transactionID_);
	}

    public void eventNotification() {
        //Do nothing, no notification is sent out.
        //Need to override the super eventNotification though.
    }
}
