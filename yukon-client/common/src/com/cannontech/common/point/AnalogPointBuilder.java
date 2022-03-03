package com.cannontech.common.point;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.point.PointLimit;
import com.google.common.collect.Maps;

public class AnalogPointBuilder extends ScalarPointBuilder {
    private final Logger log = YukonLogManager.getLogger(AnalogPointBuilder.class);
    private double multiplier = 1.0;
    private final int stateGroupId = -1; //DefaultAnalog
    private int meterDials = 0;
    private Double dataOffset = 0.0;
    private Double deadband = null;
    
    protected AnalogPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Builds an AnalogPoint object from this populated builder.
     * Stale data time and update values are ignored in this case, because the DBPersistents
     * do not support them.
     */
    @Override
    public AnalogPoint build() {
        PointBase point =
            PointFactory.createAnalogPoint(pointName,
                                           paoId,
                                           pointId,
                                           pointOffset,
                                           unitOfMeasure,
                                           multiplier,
                                           PointTemplate.DEFAULT_DATA_OFFSET,
                                           stateGroupId,
                                           decimalPlaces,
                                           archiveType,
                                           archiveInterval);
        
        AnalogPoint analogPoint = (AnalogPoint) point;
        
        analogPoint.getPointAnalog().setDataOffset(dataOffset);
        if(deadband != null) analogPoint.getPointAnalog().setDeadband(deadband);
        
        HashMap<Integer, PointLimit> limitMap = Maps.newHashMap();
        if(highLimit1 != null && lowLimit1 != null) {
            PointLimit limit1 = new PointLimit();
            limit1.setHighLimit(highLimit1);
            limit1.setLowLimit(lowLimit1);
            limit1.setLimitDuration(limitDuration1 == null ? 0 : limitDuration1);
            limit1.setLimitNumber(1);
            limit1.setPointID(pointId);
            limitMap.put(1, limit1);
        }
        if(highLimit2 != null && lowLimit2 != null) {
            PointLimit limit2 = new PointLimit();
            limit2.setHighLimit(highLimit2);
            limit2.setLowLimit(lowLimit2);
            limit2.setLimitDuration(limitDuration2 == null ? 0 : limitDuration2);
            limit2.setLimitNumber(2);
            limit2.setPointID(pointId);
            limitMap.put(2, limit2);
        }
        analogPoint.setPointLimitsMap(limitMap);
        
        analogPoint.getPointUnit().setLowReasonabilityLimit(lowReasonability);
        analogPoint.getPointUnit().setHighReasonabilityLimit(highReasonability);
        analogPoint.getPointUnit().setMeterDials(meterDials);
        
        if(isDisabled) {
            analogPoint.getPoint().setServiceFlag(CtiUtilities.getTrueCharacter());
        }
        
        if(staleDataTime != null) {
            log.warn("Build() called on builder with Stale Data settings. Stale data settings" +
                     " will be lost unless Insert() is used.");
        }
        
        return analogPoint;
    }
    
    /**
     * Builds an AnalogPoint object from this populated builder and inserts it into the database.
     * Stale data time and update values are inserted, but are not accessible through the
     * returned AnalogPoint.
     */
    @Override
    public AnalogPoint insert() {
        return (AnalogPoint) super.insert();
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setMultiplier(double multiplier) {
        if(multiplier < 0.0) throw new IllegalStateException("Multiplier cannot be negative.");
        this.multiplier = multiplier;
    }
    
    public void setDataOffset(double dataOffset) {
        this.dataOffset = dataOffset;
    }
    
    public void setDeadband(double deadband) {
        this.deadband = deadband;
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setMeterDials(int meterDials) {
        if(meterDials < 0) throw new IllegalStateException("Meter Dials cannot be negative.");
        this.meterDials = meterDials;
    }
}
