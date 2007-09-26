package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlDisabledDevicesModel;

public class CapControlDisabledDevicesReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Device Name", "deviceName", 200),
    };

    public CapControlDisabledDevicesReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlDisabledDevicesReport() {
        this(new CapControlDisabledDevicesModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
