package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Insert the type's description here.
 * Creation date: (2/11/2002 10:36:09 AM)
 * @author: 
 */
public class ThermostatSetbackGear extends com.cannontech.database.db.device.lm.LMThermostatGear 
{
	/**
	 * TimerRefreshGear constructor comment.
	 */
	public ThermostatSetbackGear() 
	{
		super();
	
		setControlMethod(GearControlMethod.ThermostatRamping);
		setGearID(super.getGearID());
	}



}