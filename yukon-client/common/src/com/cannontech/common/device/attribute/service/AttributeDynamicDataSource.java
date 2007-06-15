package com.cannontech.common.device.attribute.service;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;

public class AttributeDynamicDataSource {

    public DynamicDataSource dynamicDataSource;
    public AttributeService attributeService;
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    public PointValueHolder getPointValue(Meter meter, Attribute attribute) {
        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
        return dynamicDataSource.getPointValue(litePoint.getPointID());
    }
}
