package com.cannontech.common.pao.attribute.service;

import java.util.Collection;
import java.util.List;
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
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteHardwarePAObject;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Maps;

public class AttributeDynamicDataSource {

    @Autowired public AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired public AttributeService attributeService;
    @Autowired private PointFormattingService pointFormattingService;

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
    
    public Map<LiteHardwarePAObject, PointValueHolder> getFilteredPointValues(Collection<? extends LiteHardwarePAObject> paos, 
                                                                         Attribute attribute, List<String> filteredValues, YukonUserContext userContext) {
        Map<LiteHardwarePAObject,PointValueHolder> pointValues = Maps.newHashMapWithExpectedSize(paos.size());
        
        for (LiteHardwarePAObject hwPao : paos) {
            PointValueHolder value = getPointValue(hwPao, attribute);
            String formattedValue = pointFormattingService.getValueString(value, Format.VALUE, userContext);
            if (filteredValues.isEmpty() || filteredValues.contains(formattedValue)) {
                pointValues.put(hwPao, value);
            }
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
