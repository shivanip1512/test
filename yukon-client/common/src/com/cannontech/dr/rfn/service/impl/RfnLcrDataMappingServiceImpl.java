package com.cannontech.dr.rfn.service.impl;

import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnLcrDataMappingServiceImpl<T> implements RfnLcrDataMappingService<T> {

    private static final Logger log = YukonLogManager.getLogger(RfnLcrDataMappingServiceImpl.class);
    @Autowired protected AttributeService attributeService;

    public abstract PointData getPointData(BuiltInAttribute attribute, Double value, PaoPointIdentifier paoPointIdentifier,
            Integer pointId, Date timeOfReading);

    public PointData buildPointData(Integer pointId, Integer pointType, Date timestamp, Double value) {

        PointData pointData = new PointData();
        pointData.setTagsPointMustArchive(true);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setId(pointId);
        pointData.setType(pointType);
        pointData.setTime(timestamp);
        pointData.setValue(value);
        return pointData;
    }

    protected void createPointIfMissing(RfnDevice device, BuiltInAttribute attribute) {
        boolean pointExists = attributeService.pointExistsForAttribute(device, attribute);
        if (!pointExists) {
            log.debug("Creating point for attribute (" + attribute + ") on device: " 
                     + device.getName() + ".");
            attributeService.createPointForAttribute(device, attribute);
        }
    }

}
