package com.cannontech.database.data.capcontrol;

/**
 * @author rneuharth
 *
 * A common interface for all CapBankContollers in the System
 *
 */
public interface ICapBankController 
{
	
   public void assignAddress( Integer newAddress );
	public Integer copiableAddress();

	public void setDeviceID(Integer deviceID);
	
	//could be a port, route, etc...
	public void setCommID( Integer comID );
	public Integer getCommID();
}
