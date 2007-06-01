package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CBCInventoryModel;

public class CBCInventoryReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("CBC Name", "cbcName", 60),
        new ColumnLayoutData("IP Address", "ipAddress", 60),
        new ColumnLayoutData("Slave Address", "slaveAddress", 60),
        new ColumnLayoutData("Protocol", "protocol", 60),
        new ColumnLayoutData("Area", "region", 60),
        new ColumnLayoutData("Sub Name", "subName", 60),
        new ColumnLayoutData("Feeder Name", "feederName", 60),
        new ColumnLayoutData("Bank Name", "bankName", 60),
        new ColumnLayoutData("Bank Size", "bankSize", 60),
        new ColumnLayoutData("Control Type", "controlType", 60),
        new ColumnLayoutData("Directions", "driveDirections", 60),
    };

    public CBCInventoryReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CBCInventoryReport() {
        this(new CBCInventoryModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
