/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.LoadActionCode;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CDEvent extends MultispeakEvent{

    private String meterNumber = null;
    private LoadActionCode loadActionCode = null;
    private String resultMessage = null;

    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public CDEvent(MultispeakVendor mspVendor_, long pilMessageID_, int returnMessages_) {
        super(mspVendor_, pilMessageID_, returnMessages_);
    }
    
	/**
	 * @param mspVendor_
	 * @param pilMessageID_
	 */
	public CDEvent(MultispeakVendor mspVendor_, long pilMessageID_) {
		this(mspVendor_, pilMessageID_, 1);
	}

    public LoadActionCode getLoadActionCode() {
        return loadActionCode;
    }

    public void setLoadActionCode(LoadActionCode loadActionCode) {
        this.loadActionCode = loadActionCode;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
