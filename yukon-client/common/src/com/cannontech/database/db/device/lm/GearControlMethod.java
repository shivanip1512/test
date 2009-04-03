package com.cannontech.database.db.device.lm;

public enum GearControlMethod {
	TimeRefresh,
	SmartCycle,
	MasterCycle,
	Rotation,
	Latching,
	TrueCycle,
	MagnitudeCycle,
	TargetCycle,
	ThermostatRamping,
	SimpleThermostatRamping,
	ThermostatPreOperate,
	NoControl;
	
	static public GearControlMethod getGearControlMethod(String value) {
		for (GearControlMethod method : GearControlMethod.values()) {
			if(value.equals(method.toString())) {
				return method;
			}
		}
		return NoControl;
	}
	
	static public boolean isRamping(GearControlMethod method) {
		if(method == SimpleThermostatRamping || method == ThermostatRamping) {
			return true;
		} else {
			return false;
		}
	}
}


