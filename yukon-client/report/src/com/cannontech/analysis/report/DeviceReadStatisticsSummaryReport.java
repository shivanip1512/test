package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.DeviceReadStatisticsSummaryModel;
import com.cannontech.spring.YukonSpringHook;

public class DeviceReadStatisticsSummaryReport extends SimpleYukonReportBase {
    
    DeviceReadStatisticsSummaryModel deviceReadStatisticsSummaryModel;
    
    public DeviceReadStatisticsSummaryReport(BareReportModel bareModel) {
        super(bareModel);
        this.deviceReadStatisticsSummaryModel = (DeviceReadStatisticsSummaryModel)bareModel;
    }
    
    public DeviceReadStatisticsSummaryReport() {
        this(YukonSpringHook.getBean("deviceReadStatisticsSummaryModel", DeviceReadStatisticsSummaryModel.class));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Group Name", "groupName", 300),
            new ColumnLayoutData("Device Count", "deviceCount", 100),
            new ColumnLayoutData("Devices With Reads", "devicesWithReads", 100),
            new ColumnLayoutData("Percentage with Readings", "readPercent", 125, "###.##%"),
        };
        return Arrays.asList(bodyColumns);
    }

}
