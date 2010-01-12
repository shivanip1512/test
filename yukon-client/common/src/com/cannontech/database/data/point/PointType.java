package com.cannontech.database.data.point;

import org.apache.commons.lang.Validate;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum PointType implements DisplayableEnum, DatabaseRepresentationSource {
	Status(PointTypes.STATUS_POINT),
	Analog(PointTypes.ANALOG_POINT),
	PulseAccumulator(PointTypes.PULSE_ACCUMULATOR_POINT),
	DemandAccumulator(PointTypes.DEMAND_ACCUMULATOR_POINT),
	CalcAnalog(PointTypes.CALCULATED_POINT),
	StatusOutput(PointTypes.STATUS_OUTPUT_POINT), //5
	AnalogOutput(PointTypes.ANALOG_OUTPUT_POINT),
	System(PointTypes.SYSTEM_POINT),
	CalcStatus(PointTypes.CALCULATED_STATUS_POINT),
	;
	
	private final int pointTypeId;
    private final static ImmutableMap<Integer, PointType> lookupById;
    
    static {
        Builder<Integer, PointType> idBuilder = ImmutableMap.builder();
        for (PointType pointType : values()) {
            idBuilder.put(pointType.pointTypeId, pointType);
        }
        lookupById = idBuilder.build();
    }

	private PointType(int pointTypeId) {
		this.pointTypeId = pointTypeId;
		
	}
	
	public int getPointTypeId() {
		return pointTypeId;
	}
	
	public String getPointTypeString() {
		return PointTypes.getType(pointTypeId);
	}
	
	public static PointType getForId(int pointType) {
		PointType result = lookupById.get(pointType);
		Validate.notNull(result, Integer.toString(pointType));
		return result;
	}
	
	public static PointType getForString(String pointType) {
		int type = PointTypes.getType(pointType);
		PointType result = lookupById.get(type);
		Validate.notNull(result, pointType);
		return result;
	}

    @Override
    public String getFormatKey() {
        return "yukon.common.device.pointType." + this.name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return getPointTypeString();
    }
}
