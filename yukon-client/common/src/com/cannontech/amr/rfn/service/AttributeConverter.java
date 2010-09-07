package com.cannontech.amr.rfn.service;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface AttributeConverter {
    public BuiltInAttribute getAttribute();
    public double convertValue(double value);
}
