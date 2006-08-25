/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;

import java.util.Observable;

import com.cannontech.multispeak.client.MultispeakVendor;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MultispeakEvent extends Observable{
    
	/** The unique vendor name who initiated the event */
	private MultispeakVendor mspVendor = null;
	/** The unique messageID for all of the commands sent to pil */
	private long pilMessageID = -1;
    /** The number of commander messages this event is waiting for **/
    private int returnMessages = 0;

	/**
	 * 
	 */
	public MultispeakEvent(MultispeakVendor mspVendor_, long pilMessageID_) {
		this(mspVendor_, pilMessageID_, 1);
	}

    /**
     * 
     */
    public MultispeakEvent(MultispeakVendor mspVendor_, long pilMessageID_, int returnMessages_) {
        super();
        mspVendor = mspVendor_;
        pilMessageID = pilMessageID_;
        returnMessages = returnMessages_; 
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
    public MultispeakVendor getMspVendor()
    {
        return mspVendor;
    }

    /**
     * @param mspVendor The mspVendor to set.
     */
    public void setMspVendor(MultispeakVendor mspVendor)
    {
        this.mspVendor = mspVendor;
    }
    /**
     * @return Returns the returnMessages.
     */
    public int getReturnMessages()
    {
        return returnMessages;
    }
    /**
     * @param returnMessages The returnMessages to set.
     */
    public void setReturnMessages(int returnMessages)
    {
        this.returnMessages = returnMessages;
    }
    
    public void messageReceived() {
        if( getReturnMessages() > 0)
            returnMessages--;
    }

}
