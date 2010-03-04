package com.cannontech.common.pao.attribute.service;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
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
    
    /**
     * Returns a RichPointData object for the meter and attribute specified.
     * Requires database hit! (to retrieve the litePoint).
     * @param meter
     * @param attribute
     * @return
     */
    public RichPointData getRichPointData(Meter meter, Attribute attribute) {
        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
        PointValueQualityHolder pointValueHolder = dynamicDataSource.getPointValue(litePoint.getPointID());
        PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(litePoint, meter);
        
        RichPointData richPointData = new RichPointData(pointValueHolder, paoPointIdentifier);
        return richPointData;
    }
}
