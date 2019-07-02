package com.cannontech.database.db.device.lm;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.data.device.lm.EcobeeCycleGear;
import com.cannontech.database.data.device.lm.HoneywellCycleGear;
import com.cannontech.database.data.device.lm.ItronCycleGear;
import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.data.device.lm.MagnitudeCycleGear;
import com.cannontech.database.data.device.lm.MasterCycleGear;
import com.cannontech.database.data.device.lm.NestCriticalCycleGear;
import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.database.data.device.lm.NoControlGear;
import com.cannontech.database.data.device.lm.RotationGear;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.data.device.lm.SimpleOnOffGear;
import com.cannontech.database.data.device.lm.SepTemperatureOffsetGear;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.data.device.lm.TrueCycleGear;

public enum GearControlMethod implements DatabaseRepresentationSource, DisplayableEnum {
    TimeRefresh(TimeRefreshGear.class, "Time Refresh"),
    SmartCycle(SmartCycleGear.class, "Smart Cycle"),
    SepCycle(SepCycleGear.class, "SEP Cycle"),
    EcobeeCycle(EcobeeCycleGear.class, "ecobee Cycle"),
    HoneywellCycle(HoneywellCycleGear.class, "Honeywell Cycle"),
    ItronCycle(ItronCycleGear.class, "Itron Cycle"),
    NestCriticalCycle(NestCriticalCycleGear.class, "Nest Critical Cycle"),
    NestStandardCycle(NestStandardCycleGear.class, "Nest Standard Cycle"),
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
    SimpleOnOff(SimpleOnOffGear.class, "Simple On/Off"),
    NoControl(NoControlGear.class, "No Control");

	private Class<?> gearClass;
    private String displayName;
    private String baseKey = "yukon.web.modules.dr.gearControlMethod.";

	private GearControlMethod(Class<?> gear, String displayName) {
		this.gearClass = gear;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	static public GearControlMethod getGearControlMethod(String value) {
		try{
			return GearControlMethod.valueOf(value);
		} catch (IllegalArgumentException e) {
			return NoControl;
		}
	}
	
	public boolean isRamping() {
	    return this == SimpleThermostatRamping || this == ThermostatRamping;
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

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}