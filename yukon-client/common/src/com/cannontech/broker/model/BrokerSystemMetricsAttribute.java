package com.cannontech.broker.model;

import java.io.Serializable;

import com.cannontech.common.pao.attribute.model.Attribute;

public class BrokerSystemMetricsAttribute implements Serializable {
    private static final long serialVersionUID = 1L;

    private Attribute attribute;
    private double pointValue;

    public BrokerSystemMetricsAttribute(Attribute attribute, double pointValue) {
        this.attribute = attribute;
        this.pointValue = pointValue;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getPointValue() {
        return pointValue;
    }

}
