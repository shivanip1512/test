package com.cannontech.common.point;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.point.PointLimit;
import com.google.common.collect.Maps;

public class AccumulatorPointBuilder extends ScalarPointBuilder {
    private final Logger log = YukonLogManager.getLogger(AccumulatorPointBuilder.class);
    private AccumulatorType accumulatorType;
    private int pointOffset = 0; //no physical point offset unless specified
    private double multiplier = 1.0;
    private int stateGroupId = -2; //DefaultAccumulator
    private int meterDials = 0;
    private Double dataOffset = 0.0;
    
    protected AccumulatorPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, AccumulatorType accumulatorType, PointPropertyValueDao pointPropertyValueDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
        this.accumulatorType = accumulatorType;
    }
    
    /**
     * Builds an AccumulatorPoint object from this populated builder.
     * Stale data time and update values are ignored in this case, because the DBPersistents
     * do not support them.
     */
    @Override
    public AccumulatorPoint build() {
        AccumulatorPoint point;
        if(accumulatorType == AccumulatorType.PULSE) {
            point = (AccumulatorPoint) PointFactory.createPulseAccumPoint(pointName, paoId, pointId, 
                                                                      pointOffset, unitOfMeasure, multiplier, 
                                                                      stateGroupId, decimalPlaces, 
                                                                      archiveType, archiveInterval);
        } else {
            point = (AccumulatorPoint) PointFactory.createDmdAccumPoint(pointName, paoId, pointId, 
                                                                        pointOffset, unitOfMeasure, 
                                                                        multiplier, stateGroupId, 
                                                                        decimalPlaces, archiveType, 
                                                                        archiveInterval);
        }
        
        point.getPointUnit().setMeterDials(meterDials);
        point.getPointAccumulator().setDataOffset(dataOffset);
        
        Map<Integer, PointLimit> limitMap = Maps.newHashMap();
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
        point.setPointLimitsMap(limitMap);
        
        point.getPointUnit().setLowReasonabilityLimit(lowReasonability);
        point.getPointUnit().setHighReasonabilityLimit(highReasonability);
        
        if(isDisabled) {
            point.getPoint().setServiceFlag(CtiUtilities.getTrueCharacter());
        }
        
        if(staleDataTime != null) {
            log.warn("Build() called on builder with Stale Data settings. Stale data settings" +
                     " will be lost unless Insert() is used.");
        }
        
        return point;
    }
    
    @Override
    public AccumulatorPoint insert() {
        return (AccumulatorPoint) super.insert();
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setPointOffset(int pointOffset) {
        if(pointOffset < 0) throw new IllegalArgumentException("Point Offset cannot be negative.");
        this.pointOffset = pointOffset;
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setMultiplier(double multiplier) {
        if(multiplier < 0.0) throw new IllegalStateException("Multiplier cannot be negative.");
        this.multiplier = multiplier;
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setMeterDials(int meterDials) {
        if(meterDials < 0) throw new IllegalStateException("Meter Dials cannot be negative.");
        this.meterDials = meterDials;
    }
    
    public void setDataOffset(double dataOffset) {
        this.dataOffset = dataOffset;
    }
}
