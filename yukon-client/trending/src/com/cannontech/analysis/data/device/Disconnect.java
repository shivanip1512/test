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
	private String collGroup = null;
	private String deviceName = null;
	private String pointName = null;
	private java.util.Date timeStamp = null;
	private String valueString = null;

	/**
	 * 
	 */
	public Disconnect()
	{
		super();
	}

	/**
	 * @param collGroup_
	 * @param deviceName_
	 * @param pointName_
	 * @param pointID_
	 * @param timestamp_
	 * @param value_
	 */
	public Disconnect(String collGroup_, String deviceName_, String pointName_, java.util.Date timeStamp_, String value_)
	{
		collGroup = collGroup_;
		deviceName = deviceName_;
		pointName = pointName_;
		timeStamp = timeStamp_;
		valueString = value_;			
	}
	/**
	 * @return
	 */
	public String getCollGroup()
	{
		return collGroup;
	}

	/**
	 * @return
	 */
	public String getDeviceName()
	{
		return deviceName;
	}

	/**
	 * @return
	 */
	public String getPointName()
	{
		return pointName;
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
	public String getValueString()
	{
		return valueString;
	}

}