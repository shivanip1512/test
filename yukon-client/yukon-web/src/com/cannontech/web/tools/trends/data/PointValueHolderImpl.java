
package com.cannontech.web.tools.trends.data;

import java.util.Date;

import com.cannontech.core.dynamic.PointValueHolder;

public class PointValueHolderImpl implements PointValueHolder {
    private Date pointDataTimeStamp;
    private double pointValue;
    @Override
    public int getId() {

        return 0;

    }

    @Override
    public Date getPointDataTimeStamp() {

        return  this.pointDataTimeStamp;

    }

    @Override
    public int getType() {

        return 0;

    }

    public void setPointDataTimeStamp(Date pointDateTimeStamp) {
    
        this.pointDataTimeStamp = pointDateTimeStamp;
    }

    public void setPointValue(double pointValue) {
    
        this.pointValue = pointValue;
    }

    @Override
    public double getValue() {

        return  this.pointValue;

    }

}

