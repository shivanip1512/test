package com.cannontech.amr.crf.service;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface AttributeConverter {
    public BuiltInAttribute getAttribute();
    public double convertValue(double value);
}
