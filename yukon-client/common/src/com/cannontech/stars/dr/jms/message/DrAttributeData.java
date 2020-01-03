package com.cannontech.stars.dr.jms.message;

import java.io.Serializable;
import java.util.Date;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DrAttributeData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double value;
    private Date timeStamp;
    private BuiltInAttribute attribute;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
