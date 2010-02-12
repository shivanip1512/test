package com.cannontech.web.picker.v2;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.search.PointDeviceSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPoint;
import com.google.common.collect.Lists;

public class PointPicker extends LucenePicker<UltraLightPoint> {
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        String titleKeyPrefix = "yukon.web.modules.picker.point.";

        columns.add(new OutputColumn("pointName", titleKeyPrefix + "pointName"));
        columns.add(new OutputColumn("pointId", titleKeyPrefix + "pointId"));
        columns.add(new OutputColumn("deviceName", titleKeyPrefix + "deviceName"));

        outputColumns = Collections.unmodifiableList(columns);
    }

    private PointDeviceSearcher pointDeviceSearcher;

    @Override
    public String getIdFieldName() {
        return "pointId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public SearchResult<UltraLightPoint> search(String ss, int start,
            int count) {
        SearchResult<UltraLightPoint> hits;
        if (StringUtils.isBlank(ss)) {
            hits = pointDeviceSearcher.allPoints(criteria, start, count);
        } else {
            hits = pointDeviceSearcher.search(ss, criteria, start , count);
        }
        return hits;
    }

    @Autowired
    public void setPointDeviceSearcher(PointDeviceSearcher pointDeviceSearcher) {
        this.pointDeviceSearcher = pointDeviceSearcher;
    }
}
