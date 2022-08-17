package com.cannontech.database.data.point;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public enum PointType implements DisplayableEnum, DatabaseRepresentationSource {
    
    Status(PointTypes.STATUS_POINT), // 0
    Analog(PointTypes.ANALOG_POINT), // 1
    PulseAccumulator(PointTypes.PULSE_ACCUMULATOR_POINT), // 2
    DemandAccumulator(PointTypes.DEMAND_ACCUMULATOR_POINT), // 3
    CalcAnalog(PointTypes.CALCULATED_POINT), // 4
    StatusOutput(PointTypes.STATUS_OUTPUT_POINT), //5
    AnalogOutput(PointTypes.ANALOG_OUTPUT_POINT), // 6
    System(PointTypes.SYSTEM_POINT), // 7
    CalcStatus(PointTypes.CALCULATED_STATUS_POINT), // 8
    ;
    
    private final int pointTypeId;
    private final static ImmutableMap<Integer, PointType> lookupById;
    private final static Set<PointType> statusPoints = Sets.immutableEnumSet(Status, CalcStatus, StatusOutput);
    private final static Set<PointType> calcPoints = Sets.immutableEnumSet(CalcStatus, CalcAnalog);
    
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
        return "yukon.common.point.pointType." + this.name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return getPointTypeString();
    }

    public boolean isStatus() {
        return statusPoints.contains(this);
    }
    
    public boolean isValuePoint() {
       List<PointType> types = Lists.newArrayList(PointType.values());
       types.removeAll(statusPoints);
       types.remove(PointType.System);
       return types.contains(this);
    }
    
    public boolean hasUnitMeasure() {
        return !(isStatus() || this == System);
    }
    
    public boolean isCalcPoint() {
        return calcPoints.contains(this);
    }
}
