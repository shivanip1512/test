/*
 * Created on Feb 10, 2004
 */
package com.cannontech.analysis.data.device;

import java.util.Date;


/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PowerFail
{	
	/** Number of columns */
	private String collGroup = null;
	private String deviceName = null;
	private String pointName = null;
	private Date timestamp = null;
	private Integer powerFailCount = null;

	/**
	 * 
	 */
	public PowerFail()
	{
		super();
	}

	/**
	 * @param collGroup_
	 * @param deviceName_
	 * @param pointName_
	 * @param pointID_
	 * @param powerFailCount_
	 */
	public PowerFail(String collGroup_, String deviceName_, String pointName_, Date timestamp_, Integer powerFailCount_)
	{
		collGroup = collGroup_;
		deviceName = deviceName_;
		pointName = pointName_;
		timestamp = timestamp_;
		powerFailCount = powerFailCount_;			
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
	public Integer getPowerFailCount()
	{
		return powerFailCount;
	}

	/**
	 * @return
	 */
	public Date getTimestamp()
	{
		return timestamp;
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

}