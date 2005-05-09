/*
 * Created on Feb 10, 2004
 */
package com.cannontech.analysis.data.device;



/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Disconnect
{	
    private MeterData meterData = null;
    private String type = null;
	private java.util.Date timeStamp = null;
	private Double value = null;
	
	/**
	 * 
	 */
	public Disconnect()
	{
		super();
	}

	/**
	 * @param meterdata_
	 * @param type_
	 * @param timestamp_
	 * @param value_
	 */
	public Disconnect(MeterData meterData_, String type_, java.util.Date timeStamp_, Double value_)
	{
	    meterData = meterData_;
		type = type_;
		timeStamp = timeStamp_;
		value = value_;			
	}

	/**
	 * @return
	 */
	public java.util.Date getTimeStamp()
	{
		return timeStamp;
	}

	/**
	 * @return
	 */
	public Double getValue()
	{
		return value;
	}
	
	/**
	 * @return Returns the meterData.
	 */
	public MeterData getMeterData()
	{
	    return meterData;
	}
    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }
}