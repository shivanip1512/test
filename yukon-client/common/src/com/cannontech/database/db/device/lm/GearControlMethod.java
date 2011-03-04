package com.cannontech.database.db.device.lm;

import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.data.device.lm.MagnitudeCycleGear;
import com.cannontech.database.data.device.lm.MasterCycleGear;
import com.cannontech.database.data.device.lm.NoControlGear;
import com.cannontech.database.data.device.lm.RotationGear;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.data.device.lm.TrueCycleGear;
import com.cannontech.database.data.device.lm.TimeRefreshGear;

public enum GearControlMethod {
	TimeRefresh(TimeRefreshGear.class),
	SmartCycle(SmartCycleGear.class),
	SepCycle(SepCycleGear.class),
	MasterCycle(MasterCycleGear.class),
	Rotation(RotationGear.class),
	Latching(LatchingGear.class),
	TrueCycle(TrueCycleGear.class),
	MagnitudeCycle(MagnitudeCycleGear.class),
	TargetCycle(TargetCycleGear.class),
	ThermostatRamping(ThermostatSetbackGear.class),
	SimpleThermostatRamping(SimpleThermostatRampingGear.class),
	NoControl(NoControlGear.class);
	
	private Class<?> gearClass;
	
	private GearControlMethod(Class<?> gear) {
		this.gearClass = gear;
	}
	
	static public GearControlMethod getGearControlMethod(String value) {
		try{
			return GearControlMethod.valueOf(value);
		} catch (IllegalArgumentException e) {
			return NoControl;
		}
	}
	
	public boolean isRamping() {
		if(this == SimpleThermostatRamping || this == ThermostatRamping) {
			return true;
		} else {
			return false;
		}
	}
	
	public LMProgramDirectGear createNewGear() {
		try{
			return (LMProgramDirectGear)gearClass.newInstance();
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IllegalAccessException while from createNewGear()");
		} catch (InstantiationException e) {
			throw new RuntimeException("InstantiationException while from createNewGear()");
		}
	}
}


