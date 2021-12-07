package com.cannontech.util;

import com.cannontech.azure.model.IOTDataType;
import com.cannontech.message.model.SystemData;
import com.cannontech.message.model.YukonMetric;
import com.cannontech.message.model.YukonMetricIOTDataType;

public class SystemDataConverterHelper {

    public static SystemData convert(YukonMetric yukonMetric) {
        SystemData systemData = new SystemData();
        systemData.setFieldName(YukonMetricIOTDataType.valueOf(yukonMetric.getPointInfo().name()).getFieldName());
        systemData.setFieldValue(yukonMetric.getValue());
        systemData.setIotDataType(IOTDataType.TELEMETRY);
        systemData.setTimestamp(yukonMetric.getTimestamp());
        return systemData;
    }

}
