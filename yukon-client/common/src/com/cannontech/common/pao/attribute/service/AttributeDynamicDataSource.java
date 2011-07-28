package com.cannontech.common.pao.attribute.service;

import java.util.Collection;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Maps;

public class AttributeDynamicDataSource {

    public DynamicDataSource dynamicDataSource;
    public AttributeService attributeService;
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    public PointValueHolder getPointValue(YukonPao pao, Attribute attribute) {
        LitePoint litePoint = attributeService.getPointForAttribute(pao, attribute);
        return dynamicDataSource.getPointValue(litePoint.getPointID());
    }
    
    public Map<PaoIdentifier,PointValueHolder> getPointValues(Collection<? extends YukonPao> paos, Attribute attribute) {
        Map<PaoIdentifier,PointValueHolder> pointValues = Maps.newHashMapWithExpectedSize(paos.size());
        
        for (YukonPao pao : paos) {
            PointValueHolder value = getPointValue(pao, attribute);
            pointValues.put(pao.getPaoIdentifier(), value);
        }
        
        return pointValues;
    }
    
    /**
     * Returns a RichPointData object for the pao and attribute specified.
     * Requires database hit! (to retrieve the litePoint).
     * 
     * @param pao
     * @param attribute
     * @return
     */
    public RichPointData getRichPointData(YukonPao pao, Attribute attribute) {
        LitePoint litePoint = attributeService.getPointForAttribute(pao, attribute);
        PointValueQualityHolder pointValueHolder = dynamicDataSource.getPointValue(litePoint.getPointID());
        PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(litePoint, pao);
        
        RichPointData richPointData = new RichPointData(pointValueHolder, paoPointIdentifier);
        return richPointData;
    }
}
