/*
 * Created on Feb 10, 2004
 */
package com.cannontech.analysis.data.device;


/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/** Inner class container of table model data*/
public class Carrier
{	
	private String paoName = null;
	private String paoType = null;
	private String address = null;			
	private String routeName = null;
	private String collGroup = null;
	private String testCollGroup = null;
	
	/**
	 * 
	 */
	public Carrier()
	{
		super();
	}

	/**
	 * @param paoName_
	 * @param paoType_
	 * @param address_
	 * @param routeName_
	 * @param collGroup_
	 * @param testCollGroup_
	 */
	public Carrier(String paoName_, String paoType_, String address_, String routeName_, String collGroup_, String testCollGroup_)
	{
		paoName = paoName_;
		paoType = paoType_;
		address = address_;
		routeName = routeName_;
		collGroup = collGroup_;
		testCollGroup = testCollGroup_;
	}
	/**
	 * @return
	 */
	public String getAddress()
	{
		return address;
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
	public String getPaoName()
	{
		return paoName;
	}

	/**
	 * @return
	 */
	public String getPaoType()
	{
		return paoType;
	}

	/**
	 * @return
	 */
	public String getRouteName()
	{
		return routeName;
	}

	/**
	 * @return
	 */
	public String getTestCollGroup()
	{
		return testCollGroup;
	}

}