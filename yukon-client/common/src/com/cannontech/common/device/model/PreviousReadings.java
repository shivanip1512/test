package com.cannontech.common.device.model;

import java.util.Date;
import java.util.List;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dynamic.PointValueHolder;

public class PreviousReadings {
    private Attribute attribute;
    private Date cutoffDate;
    private List<PointValueHolder> previous36;
    private List<PointValueHolder> previous3Months;
    
    public PreviousReadings() {}
    public PreviousReadings(Attribute attribute, Date cutoffDate, List<PointValueHolder> previous36,
                            List<PointValueHolder> previous3Months) {
        this.attribute = attribute;
        this.cutoffDate = cutoffDate;
        this.previous36 = previous36;
        this.previous3Months = previous3Months;
    }

    public Attribute getAttribute() {
        return attribute;
    }
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    
    public Date getCutoffDate() {
        return cutoffDate;
    }
    public void setCutoffDate(Date cutoffDate) {
        this.cutoffDate = cutoffDate;
    }
    
    public List<PointValueHolder> getPrevious36() {
        return previous36;
    }
    public void setPrevious36(List<PointValueHolder> previous36) {
        this.previous36 = previous36;
    }
    
    public List<PointValueHolder> getPrevious3Months() {
        return previous3Months;
    }
    public void setPrevious3Months(List<PointValueHolder> previous3Months) {
        this.previous3Months = previous3Months;
    }
}

