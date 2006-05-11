package com.cannontech.common.exception;

import com.cannontech.database.data.lite.LitePoint;

public class PointDataException extends PointException {

    public PointDataException(LitePoint point) {
        super("no point data found for point: " + point);
    }

}
