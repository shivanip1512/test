package com.cannontech.yukon.system.metrics.message;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cannontech.database.data.point.PointType;

public class YukonMetricPointDataTypeTest {
    @Test
    public void testIsYukonMetricTypeDisplyable() {
        assertFalse(YukonMetricPointDataType.isYukonMetricTypeDisplayable(-1, PointType.Analog));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.isYukonMetricTypeDisplayable(null, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.isYukonMetricTypeDisplayable(1, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.isYukonMetricTypeDisplayable(null, PointType.Analog);
        });
    }

    @Test
    public void testGetChartInterval() {
        for (YukonMetricPointDataType type : YukonMetricPointDataType.values()) {
            if (YukonMetricPointDataType.isYukonMetricTypeDisplayable(type.getOffset(), type.getType())) {
                assertNotNull(YukonMetricPointDataType.getChartInterval(type.getOffset(), type.getType()),
                        "Chart interval can not be null.");
            }
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getChartInterval(-1, PointType.Analog);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getChartInterval(null, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getChartInterval(1, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getChartInterval(null, PointType.Analog);
        });
    }

    @Test
    public void testGetConverterType() {
        for (YukonMetricPointDataType type : YukonMetricPointDataType.values()) {
            if (YukonMetricPointDataType.isYukonMetricTypeDisplayable(type.getOffset(), type.getType())) {
                assertNotNull(YukonMetricPointDataType.getConverterType(type.getOffset(), type.getType()),
                        "Converter type can not be null.");
            }
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getConverterType(-1, PointType.Analog);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getConverterType(null, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getConverterType(1, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            YukonMetricPointDataType.getConverterType(null, PointType.Analog);
        });
    }

}
