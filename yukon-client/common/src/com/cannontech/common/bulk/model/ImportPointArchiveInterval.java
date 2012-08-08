package com.cannontech.common.bulk.model;

import com.cannontech.database.data.point.PointArchiveInterval;

/**
 * Used for point import validation of PointArchiveInterval values. Its valueOf method pretends
 * PointArchiveInterval.ZERO doesn't exist.
 */
public class ImportPointArchiveInterval {
    public static PointArchiveInterval valueOf(String string) {
        PointArchiveInterval interval = PointArchiveInterval.valueOf(string);
        if(interval == PointArchiveInterval.ZERO) throw new IllegalArgumentException();
        return interval;
    }
}
