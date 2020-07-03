package com.cannontech.common.point;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.model.AnalogPointUpdateType;
import com.cannontech.common.bulk.model.PointPeriodicRate;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.point.PointLimit;
import com.google.common.collect.Maps;

public class CalcAnalogPointBuilder extends ScalarPointBuilder {
    private PaoDao paoDao;
    private final Logger log = YukonLogManager.getLogger(CalcAnalogPointBuilder.class);
    private PointCalculation calculation = new PointCalculation();
    private AnalogPointUpdateType updateType = AnalogPointUpdateType.ON_FIRST_CHANGE;
    private PointPeriodicRate updateRate = PointPeriodicRate.ONE_SECOND;
    private boolean forceQualityNormal = false;
    
    private int pointOffset = 0; //no physical point offset unless specified
    private final int stateGroupId = -1; //DefaultAnalog
    private int meterDials = 0;
    
    protected CalcAnalogPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao, PaoDao paoDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
        this.paoDao = paoDao;
    }
    
    /**
     * Builds an CalculatedPoint object from this populated builder and inserts it into the database.
     * Stale data time and update values are inserted, but are not accessible through the
     * returned CalculatedPoint.
     */
    public CalculatedPoint insert() {
        return (CalculatedPoint) super.insert();
    }
    
    /**
     * Builds a CalculatedPoint object from this populated builder.
     * Stale data time and update values are ignored in this case, because the DBPersistents
     * do not support them.
     */
    public CalculatedPoint build() {
        PaoIdentifier paoIdentifier = paoDao.getYukonPao(paoId).getPaoIdentifier();
        
        CalculatedPoint point = (CalculatedPoint) PointFactory.createCalculatedPoint(paoIdentifier, 
                                                                                     pointName, 
                                                                                     stateGroupId, 
                                                                                     unitOfMeasure, 
                                                                                     decimalPlaces, 
                                                                                     archiveType, 
                                                                                     archiveInterval, 
                                                                                     null);
        
        point.setCalcComponents(calculation.copyComponentsAndInsertPointId(pointId));
        point.getCalcBase().setUpdateType(updateType.getDatabaseRepresentation());
        point.getCalcBase().setPeriodicRate(updateRate.getSeconds());
        point.getPoint().setPointOffset(pointOffset);
        
        String calculateQuality = CtiUtilities.falseChar.toString();
        if(forceQualityNormal) calculateQuality = CtiUtilities.trueChar.toString();
        point.getCalcBase().setCalculateQuality(calculateQuality);
        
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
        point.setPointLimitsMap(limitMap);
        
        point.getPointUnit().setLowReasonabilityLimit(lowReasonability);
        point.getPointUnit().setHighReasonabilityLimit(highReasonability);
        point.getPointUnit().setMeterDials(meterDials);
        
        if(isDisabled) {
            point.getPoint().setServiceFlag(CtiUtilities.getTrueCharacter());
        }
        
        if(staleDataTime != null) {
            log.warn("Build() called on builder with Stale Data settings. Stale data settings" +
                     " will be lost unless Insert() is used.");
        }
        
        return point;
    }
    
    public void setForceQualityNormal(boolean forceQualityNormal) {
        this.forceQualityNormal = forceQualityNormal;
    }
    
    public void setCalculation(PointCalculation calculation) {
        this.calculation = calculation;
    }
    
    public void setUpdateType(AnalogPointUpdateType updateType) {
        this.updateType = updateType;
    }
    
    public void setUpdateRate(PointPeriodicRate updateRate) {
        this.updateRate = updateRate;
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setPointOffset(int pointOffset) {
        if(pointOffset < 0) throw new IllegalArgumentException("Point Offset cannot be negative.");
        this.pointOffset = pointOffset;
    }
}