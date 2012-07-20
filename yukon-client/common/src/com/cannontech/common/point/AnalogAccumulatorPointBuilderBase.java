package com.cannontech.common.point;

import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * Abstract base class containing functionality shared by analog, calcAnalog and accumulator point
 * builders.
 */
public abstract class AnalogAccumulatorPointBuilderBase extends PointBuilder {
    protected PointArchiveType archiveType = PointArchiveType.NONE;
    protected PointArchiveInterval archiveInterval = PointArchiveInterval.ZERO;
    protected int unitOfMeasure = UnitOfMeasure.KW.getId();
    protected int decimalPlaces = 0;
    protected Double highReasonability = null;
    protected Double lowReasonability = null;
    protected Double highLimit1 = null;
    protected Double lowLimit1 = null;
    protected Integer limitDuration1 = null;
    protected Double highLimit2 = null;
    protected Double lowLimit2 = null;
    protected Integer limitDuration2 = null;
    
    protected AnalogAccumulatorPointBuilderBase(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Must be a non-negative value.
     */
    public void unitOfMeasure(int unitOfMeasure) {
        if(unitOfMeasure < 0) throw new IllegalArgumentException("Invalid Unit Of Measure index: " + unitOfMeasure);
        this.unitOfMeasure = unitOfMeasure;
    }
    
    /**
     * Must be a non-negative value.
     */
    public void decimalPlaces(int decimalPlaces) {
        if(decimalPlaces < 0) throw new IllegalArgumentException("Decimal Places cannot be negative.");
        this.decimalPlaces = decimalPlaces;
    }
    
    public void archiveType(PointArchiveType archiveType) {
        this.archiveType = archiveType;
    }
    
    public void archiveInterval(PointArchiveInterval archiveInterval) {
        this.archiveInterval = archiveInterval;
    }
    
    public void highLimit1(double highLimit1) {
        this.highLimit1 = highLimit1;
    }
    
    public void lowLimit1(double lowLimit1) {
        this.lowLimit1 = lowLimit1;
    }
    
    public void limitDuration1(int limitDuration1) {
        this.limitDuration1 = limitDuration1;
    }
    
    public void highLimit2(double highLimit2) {
        this.highLimit2 = highLimit2;
    }
    
    public void lowLimit2(double lowLimit2) {
        this.lowLimit2 = lowLimit2;
    }
    
    public void limitDuration2(int limitDuration2) {
        this.limitDuration2 = limitDuration2;
    }
    
    public void highReasonability(double highReasonability) {
        this.highReasonability = highReasonability;
    }
    
    public void lowReasonability(double lowReasonability) {
        this.lowReasonability = lowReasonability;
    }
}
