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
	private String meterNumber = null;
	private String address = null;
	private String pointName = null;
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
	 * @param meterNumber_
	 * @param address_
	 * @param pointName_
	 * @param pointID_
	 * @param routeName_
	 */
	public MeterData(String collGroup_, String deviceName_, String meterNumber_, String address_, String pointName_, String routeName_)
	{
		collGroup = collGroup_;
		deviceName = deviceName_;
		meterNumber = meterNumber_;
		address = address_;
		pointName = pointName_;
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

    /**
     * @return Returns the address.
     */
    public String getAddress()
    {
        return address;
    }
    /**
     * @return Returns the meterNumber.
     */
    public String getMeterNumber()
    {
        return meterNumber;
    }
}