package com.cannontech.dr.rfn.service.impl;

import java.util.Date;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnLcrDataMappingServiceImpl<T> implements RfnLcrDataMappingService<T> {

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

    public PointData getPointData(BuiltInAttribute attribute, Double value, PaoPointIdentifier paoPointIdentifier,
            Integer pointId, Date timeOfReading) {
        if (attribute == BuiltInAttribute.SERVICE_STATUS) {
            /**
             * Adjust value for state group 'LCR Service Status'
             * The service status is represented in two bits:
             * 00 (decimal value 0) - State name: 'In Service', RawState: 1
             * 01 (decimal value 1) - State name: 'Temporarily Out of Service', RawState: 3
             * 10 (decimal value 2) - State Name: 'Out of Service', RawState: 2
             */
            if (value == 0) {
                value = 1.0;
            } else if (value == 1) {
                value = 3.0;
            }
        }
        Integer pointTypeId = paoPointIdentifier.getPointIdentifier().getPointType().getPointTypeId();
        return buildPointData(pointId, pointTypeId, timeOfReading, value);
    }
}
