package com.cannontech.analysis.data.device;


/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MeterData
{	
	private String collGroup = null;
	private String deviceName = null;
	private String pointName = null;
	private Integer pointID = null;
	private String routeName = null;

	/**
	 * 
	 */
	public MeterData()
	{
		super();
	}

	/**
	 * @param collGroup_
	 * @param deviceName_
	 * @param pointName_
	 * @param pointID_
	 * @param routeName_
	 */
	public MeterData(String collGroup_, String deviceName_, String pointName_, Integer pointID_, String routeName_)
	{
		collGroup = collGroup_;
		deviceName = deviceName_;
		pointName = pointName_;
		pointID = pointID_;
		routeName = routeName_;			
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
	public Integer getPointID()
	{
		return pointID;
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
	public String getRouteName()
	{
		return routeName;
	}

}