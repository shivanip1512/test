/*
 * Created on Mar 26, 2004
 */
package com.cannontech.analysis.data.device;


/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/** Inner class container of table model data*/
public class CarrierRouteMacro
{	
	private String routeMacroName = null;
	private String routeName = null;
	private String transmitterName = null;			
	private String ccuBusNumber = null;
	private String ampUse = null;
	private String fixedBits = null;
	private String variableBits = null;
	private String defaultRoute = null;
	
	/**
	 * 
	 */
	public CarrierRouteMacro()
	{
		super();
	}

	/**
	 * @param routeMacroName_
	 * @param routeName_
	 * @param transmitterName_
	 * @param ccuBusNumber_
	 * @param ampUse_
	 * @param fixedBits_
	 * @param variableBits_
	 * @param defaultRoute_
	 */
	public CarrierRouteMacro(String routeMacroName_, String routeName_, String transmitterName_, String ccuBusNumber_, String ampUse_, String fixedBits_, String variableBits_, String defaultRoute_)
	{
		routeMacroName = routeMacroName_;
		routeName = routeName_;
		transmitterName = transmitterName_;
		ccuBusNumber = ccuBusNumber_;
		ampUse = ampUse_;
		fixedBits = fixedBits_;
		variableBits = variableBits_;
		defaultRoute = defaultRoute_;
	}
	/**
	 * @return
	 */
	public String getAmpUse()
	{
		return ampUse;
	}

	/**
	 * @return
	 */
	public String getCcuBusNumber()
	{
		return ccuBusNumber;
	}

	/**
	 * @return
	 */
	public String getDefaultRoute()
	{
		return defaultRoute;
	}

	/**
	 * @return
	 */
	public String getFixedBits()
	{
		return fixedBits;
	}

	/**
	 * @return
	 */
	public String getRouteMacroName()
	{
		return routeMacroName;
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
	public String getTransmitterName()
	{
		return transmitterName;
	}

	/**
	 * @return
	 */
	public String getVariableBits()
	{
		return variableBits;
	}

}