package com.cannontech.yukon.system.metrics.message;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cannontech.database.data.point.PointType;

public class YukonMetricPointDataTypeTest {
    @Test
    public void testIsYukonMetricType() {
        for (YukonMetricPointDataType type : YukonMetricPointDataType.values()) {
            assertTrue(YukonMetricPointDataType.isYukonMetricType(type.getOffset(), type.getType()));
        }
        assertFalse(YukonMetricPointDataType.isYukonMetricType(-1, PointType.Analog));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.isYukonMetricType(null, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.isYukonMetricType(1, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.isYukonMetricType(null, PointType.Analog);
        });
    }

}
