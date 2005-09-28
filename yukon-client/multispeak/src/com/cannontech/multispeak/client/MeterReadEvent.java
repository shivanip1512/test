/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;


import com.cannontech.multispeak.MeterRead;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MeterReadEvent extends MultispeakEvent{

    private MeterRead meterRead = null;
	/**
	 * 
	 */
	public MeterReadEvent(String vendorName_, long pilMessageID_) {
		super(vendorName_, pilMessageID_);
	}
    public MeterRead getMeterRead()
    {
        return meterRead;
    }
    public void setMeterRead(MeterRead meterRead)
    {
        this.meterRead = meterRead;
    }
}
