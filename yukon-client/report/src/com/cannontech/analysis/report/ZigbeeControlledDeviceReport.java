package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.ZigbeeControlledDeviceModel;

public class ZigbeeControlledDeviceReport  extends SimpleYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Event", "eventId", 45),
        new ColumnLayoutData("Group Name", "loadGroupName", 150),
        new ColumnLayoutData("Device Name", "deviceName", 150),
        new ColumnLayoutData("Received", "ack", 50),
        new ColumnLayoutData("Canceled", "canceled", 50),
        new ColumnLayoutData("Start Time", "start", 125),
        new ColumnLayoutData("Stop Time", "stop", 125),
    };

    public ZigbeeControlledDeviceReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public ZigbeeControlledDeviceReport() {
        this(new ZigbeeControlledDeviceModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
