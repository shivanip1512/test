/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event.v4;

import com.cannontech.multispeak.client.MultispeakVendor;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class MultispeakEvent implements MspEvent{
    
	/** The unique vendor name who initiated the event */
	private MultispeakVendor mspVendor = null;
	/** The unique messageID for all of the commands sent to pil */
	private long pilMessageID = -1;
    /** The number of commander messages this event is waiting for **/
    private int returnMessages = 0;
    /** The transactionID provided from the calling web service request. */
    private String transactionID = null;
    /** The responseURL to send notification message to **/
    private String responseUrl = null;
	/**
	 * 
	 */
	public MultispeakEvent(MultispeakVendor mspVendor_, long pilMessageID_, String transactionID_, String responseUrl) {
		this(mspVendor_, pilMessageID_, 1, transactionID_, responseUrl);
	}

    /**
     * 
     */
    public MultispeakEvent(MultispeakVendor mspVendor_, long pilMessageID_, 
            int returnMessages_, String transactionID_, String responseUrl) {
        super();
        mspVendor = mspVendor_;
        pilMessageID = pilMessageID_;
        returnMessages = returnMessages_;
        transactionID = transactionID_;
        this.responseUrl = responseUrl;
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
     * @return Returns the mspVendor.
     */
    public MultispeakVendor getMspVendor() {
        return mspVendor;
    }

    /**
     * @param mspVendor The mspVendor to set.
     */
    public void setMspVendor(MultispeakVendor mspVendor) {
        this.mspVendor = mspVendor;
    }
    
    /**
     * @return Returns the returnMessages.
     */
    public int getReturnMessages() {
        return returnMessages;
    }
    
    /**
     * @param returnMessages The returnMessages to set.
     */
    public void setReturnMessages(int returnMessages) {
        this.returnMessages = returnMessages;
    }
    
    /**
     * @return Returns the transactionID of the calling web service.
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * @param transactionID The transactionID to set.
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getResponseUrl() {
        return responseUrl;
    }
    
    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }
    
    public void updateReturnMessageCount() {
        if( getReturnMessages() > 0)
            returnMessages--;
    }
}
