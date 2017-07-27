package com.cannontech.web.picker;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.search.result.UltraLightMonitor;
import com.google.common.collect.Lists;

public class MonitorPicker extends LucenePicker<UltraLightMonitor> {

    private static String titleKeyPrefix = "yukon.web.picker.monitor.";
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("monitorName", titleKeyPrefix + "name"));
        columns.add(new OutputColumn("type", titleKeyPrefix + "type"));
        columns.add(new OutputColumn("subId", titleKeyPrefix + "id"));
        outputColumns = Collections.unmodifiableList(columns);
    }
    
    @Override
    public String getIdFieldName() {
        return "id";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

}
