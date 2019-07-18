package com.cannontech.common.pao.attribute.service;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.collect.Maps;

public class AttributeDynamicDataSource {

    @Autowired public AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired public AttributeService attributeService;
        
    public PointValueHolder getPointValue(YukonPao pao, Attribute attribute) {
        LitePoint litePoint = attributeService.getPointForAttribute(pao, attribute);
        return asyncDynamicDataSource.getPointValue(litePoint.getPointID());
    }
    
    public Map<PaoIdentifier,PointValueHolder> getPointValues(Collection<? extends YukonPao> paos, Attribute attribute) {
        Map<PaoIdentifier,PointValueHolder> pointValues = Maps.newHashMapWithExpectedSize(paos.size());
        
        for (YukonPao pao : paos) {
            PointValueHolder value = getPointValue(pao, attribute);
            pointValues.put(pao.getPaoIdentifier(), value);
        }
        
        return pointValues;
    }
    
    public Map<LiteYukonPAObject,PointValueHolder> getPaoPointValues(Collection<? extends LiteYukonPAObject> paos, Attribute attribute) {
        Map<LiteYukonPAObject,PointValueHolder> pointValues = Maps.newHashMapWithExpectedSize(paos.size());
        
        for (LiteYukonPAObject pao : paos) {
            PointValueHolder value = getPointValue(pao, attribute);
            pointValues.put(pao, value);
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
        PointValueQualityHolder pointValueHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());
        PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(litePoint, pao);
        
        RichPointData richPointData = new RichPointData(pointValueHolder, paoPointIdentifier);
        return richPointData;
    }
}
