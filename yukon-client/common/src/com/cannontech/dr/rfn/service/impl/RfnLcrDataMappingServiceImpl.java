package com.cannontech.dr.rfn.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnLcrDataMappingServiceImpl<T> implements RfnLcrDataMappingService<T> {

    @Autowired protected AttributeService attributeService;

    public abstract PointData getPointData(BuiltInAttribute attribute, Double value, LitePoint point, Date timeOfReading);

    public PointData buildPointData(LitePoint point, Date timestamp, Double value) {

        PointData pointData = new PointData();
        pointData.setTagsPointMustArchive(true);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setId(point.getPointID());
        pointData.setType(point.getPointType());
        pointData.setTime(timestamp);
        pointData.setValue(value);
        return pointData;
    }
}
