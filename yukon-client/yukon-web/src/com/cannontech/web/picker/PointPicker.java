package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.result.UltraLightPoint;
import com.google.common.collect.Lists;

public class PointPicker extends LucenePicker<UltraLightPoint> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.picker.point.";

        columns.add(new OutputColumn("pointName", titleKeyPrefix + "pointName"));
        columns.add(new OutputColumn("pointId", titleKeyPrefix + "pointId"));
        columns.add(new OutputColumn("deviceName", titleKeyPrefix + "deviceName"));
        columns.add(new OutputColumn("pointType", titleKeyPrefix + "pointType"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    @Override
    public String getIdFieldName() {
        return "pointId";
    }

    @Override
    protected String getLuceneIdFieldName() {
        return "pointid";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }
}
