package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.UltraLightPoint;
import com.google.common.collect.Lists;

public class PointPicker extends LucenePicker<UltraLightPoint> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.point.";

        columns.add(new OutputColumn("pointName", titleKeyPrefix + "pointName"));
        columns.add(new OutputColumn("pointId", titleKeyPrefix + "pointId"));
        columns.add(new OutputColumn("deviceName", titleKeyPrefix + "deviceName"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    @Override
    public String getIdFieldName() {
        return "pointId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
}
