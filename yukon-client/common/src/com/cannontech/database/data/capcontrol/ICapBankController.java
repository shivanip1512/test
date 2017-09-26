package com.cannontech.database.data.capcontrol;

/**
 * @author rneuharth
 *
 * A common interface for all CapBankContollers in the System
 *
 */
public interface ICapBankController 
{
	public void setAddress( Integer newAddress );
	public Integer getAddress();

	//could be a port, route, etc...
	public void setCommID( Integer comID );
	public Integer getCommID();
}
