package com.cannontech.web.tools.points;

import org.springframework.core.convert.converter.Converter;

import com.cannontech.database.data.point.PointType;

public class StringToPointTypeConverter implements Converter<String, PointType> {

    @Override
    public PointType convert(String pointTypeString) {
        try {
            return PointType.valueOf(pointTypeString);
        } catch (IllegalArgumentException e) {
            return PointType.Analog;
        }
    }
}