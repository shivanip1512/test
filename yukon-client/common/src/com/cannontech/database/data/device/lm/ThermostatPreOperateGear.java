package com.cannontech.database.data.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 10:36:09 AM)
 * @author: 
 */
public class ThermostatPreOperateGear extends com.cannontech.database.db.device.lm.LMThermostatGear {
/**
 * TimerRefreshGear constructor comment.
 */
public ThermostatPreOperateGear() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/11/2002 10:37:20 AM)
 */
private void initialize() 
{
	setControlMethod( THERMOSTAT_SETBACK );
	setGearID(super.getGearID());
}
}
