package com.cannontech.database.data.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 10:36:09 AM)
 * @author: 
 */
public class ThermostatPreOperateGear extends com.cannontech.database.db.device.lm.LMThermostatGear 
{
	/**
	 * ThermostatPreOperateGear constructor comment.
	 */
	public ThermostatPreOperateGear() 
	{
		super();
	
		setControlMethod( THERMOSTAT_SETBACK );
		setGearID(super.getGearID());
	}
	
}
