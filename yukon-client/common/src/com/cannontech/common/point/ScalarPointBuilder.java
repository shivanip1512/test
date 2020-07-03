package com.cannontech.common.point;

import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * Abstract base class containing functionality shared by analog, calcAnalog and accumulator point
 * builders.
 */
public abstract class ScalarPointBuilder extends PointBuilder {
    protected PointArchiveType archiveType = PointArchiveType.NONE;
    protected PointArchiveInterval archiveInterval = PointArchiveInterval.ZERO;
    protected int unitOfMeasure = UnitOfMeasure.KW.getId();
    protected int decimalPlaces = 0;
    protected double highReasonability = CtiUtilities.INVALID_MAX_DOUBLE; //these values cause the reasonability
    protected double lowReasonability = CtiUtilities.INVALID_MIN_DOUBLE;  //boxes to be unchecked
    protected Double highLimit1 = null;
    protected Double lowLimit1 = null;
    protected Integer limitDuration1 = null;
    protected Double highLimit2 = null;
    protected Double lowLimit2 = null;
    protected Integer limitDuration2 = null;
    
    protected ScalarPointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao) {
        super(paoId, pointId, pointName, isDisabled, pointPropertyValueDao);
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setUnitOfMeasure(int unitOfMeasure) {
        if(unitOfMeasure < 0) throw new IllegalArgumentException("Invalid Unit Of Measure index: " + unitOfMeasure);
        this.unitOfMeasure = unitOfMeasure;
    }
    
    /**
     * Must be a non-negative value.
     */
    public void setDecimalPlaces(int decimalPlaces) {
        if(decimalPlaces < 0) throw new IllegalArgumentException("Decimal Places cannot be negative.");
        this.decimalPlaces = decimalPlaces;
    }
    
    public void setArchiveType(PointArchiveType archiveType) {
        this.archiveType = archiveType;
    }
    
    public void setArchiveInterval(PointArchiveInterval archiveInterval) {
        this.archiveInterval = archiveInterval;
    }
    
    public void setHighLimit1(double highLimit1) {
        this.highLimit1 = highLimit1;
    }
    
    public void setLowLimit1(double lowLimit1) {
        this.lowLimit1 = lowLimit1;
    }
    
    public void setLimitDuration1(int limitDuration1) {
        this.limitDuration1 = limitDuration1;
    }
    
    public void setHighLimit2(double highLimit2) {
        this.highLimit2 = highLimit2;
    }
    
    public void setLowLimit2(double lowLimit2) {
        this.lowLimit2 = lowLimit2;
    }
    
    public void setLimitDuration2(int limitDuration2) {
        this.limitDuration2 = limitDuration2;
    }
    
    public void setHighReasonability(double highReasonability) {
        this.highReasonability = highReasonability;
    }
    
    public void setLowReasonability(double lowReasonability) {
        this.lowReasonability = lowReasonability;
    }
}
