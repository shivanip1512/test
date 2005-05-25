/*
 * Created on May 24, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.capcontrol;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InputValidater
{
	public int from;
	public int to;
	public String route;
	public String banksize;
	public String manufacturer;
	public String switchType;
	public String conType;
	
	public InputValidater(int fromvar, int tovar, String routevar, String banksizevar, String manufacturervar, String switchTypevar, String conTypevar)
	{
		from = fromvar;
		to = tovar;
		route = routevar;
		banksize = banksizevar;
		manufacturer = manufacturervar;
		switchType = switchTypevar;
		conType = conTypevar;
	}
	
	public boolean checkRange(){
		return true;
	}
	
	public boolean checkRoute()
	{
		return true;	
	}
	
	public boolean checkBankSize()
	{
		return true;
	}
	
	public boolean checkManufacturer()
	{
		return true;
	}
	
	public boolean checkSwitchType()
	{
		return true;
	}
	
	public boolean checkConType()
	{
		return true;
	}
	
	public int getFrom(){
		return from;
	}
	
	public int getTo(){
		return to;
	}
	
	public String getRoute(){
		return route;
	}
	
	public String getBankSize(){
		return banksize;
	}
	
	public String getSwitchMan(){
		return manufacturer;
	}
	
	public String getSwitchType(){
		return switchType;
	}
	
	public String getControllerType(){
		return conType;
	}
}
