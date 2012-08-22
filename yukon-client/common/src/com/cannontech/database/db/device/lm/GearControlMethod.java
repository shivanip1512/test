package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.data.device.lm.MagnitudeCycleGear;
import com.cannontech.database.data.device.lm.MasterCycleGear;
import com.cannontech.database.data.device.lm.NoControlGear;
import com.cannontech.database.data.device.lm.RotationGear;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.data.device.lm.SepTemperatureOffsetGear;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.data.device.lm.TrueCycleGear;

public enum GearControlMethod implements DatabaseRepresentationSource {
    TimeRefresh(TimeRefreshGear.class, "Time Refresh"),
    SmartCycle(SmartCycleGear.class, "Smart Cycle"),
    SepCycle(SepCycleGear.class, "SEP Cycle"),
    SepTemperatureOffset(SepTemperatureOffsetGear.class, "SEP Temperature Offset"),
    MasterCycle(MasterCycleGear.class, "Master Cycle"),
    Rotation(RotationGear.class, "Rotation"),
    Latching(LatchingGear.class, "Latching"),
    TrueCycle(TrueCycleGear.class, "True Cycle"),
    MagnitudeCycle(MagnitudeCycleGear.class, "Magnitude Cycle"),    
    TargetCycle(TargetCycleGear.class, "Target Cycle"),
    ThermostatRamping(ThermostatSetbackGear.class, "Thermostat Ramping"),
    SimpleThermostatRamping(SimpleThermostatRampingGear.class, "Simple Thermostat Ramping"),
    BeatThePeak(BeatThePeakGear.class, "Beat The Peak"),
    NoControl(NoControlGear.class, "No Control");

	private Class<?> gearClass;
	private String displayName;

	private GearControlMethod(Class<?> gear, String displayName) {
		this.gearClass = gear;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	};
	
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
	
	/** Use the actual enum to write to the db, not the display string. **/
	@Override
	public Object getDatabaseRepresentation() {
		return name();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
}