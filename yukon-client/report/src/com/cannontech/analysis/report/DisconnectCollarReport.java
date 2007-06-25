package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.DisconnectCollarModel;;

public class DisconnectCollarReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Device Name", "deviceName", 200),
        new ColumnLayoutData("Device Type", "deviceType", 80),
        new ColumnLayoutData("Meter Number", "meterNumber", 80),
        new ColumnLayoutData("Disconnect Address", "disconnectAddress", 120),
    };

    public DisconnectCollarReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public DisconnectCollarReport() {
        this(new DisconnectCollarModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

