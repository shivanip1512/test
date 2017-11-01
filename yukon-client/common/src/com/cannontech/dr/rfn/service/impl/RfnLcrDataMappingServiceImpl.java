package com.cannontech.dr.rfn.service.impl;

import java.util.Date;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnLcrDataMappingServiceImpl<T> implements RfnLcrDataMappingService<T> {

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

}
