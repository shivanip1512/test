package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlMaintenancePendingModel;

public class CapControlMaintenancePendingReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("CBC", "cbcName", 60),
        new ColumnLayoutData("Cap Bank", "capBankName", 80),
        new ColumnLayoutData("Address", "address", 80),
        new ColumnLayoutData("Driving Directions", "drivingDirections", 90),
        new ColumnLayoutData("Maintenance Area", "maintenanceAreaId", 90),
        new ColumnLayoutData("Pole #", "poleNumber", 45),
        new ColumnLayoutData("Latitude", "latitude", 45),
        new ColumnLayoutData("Longitude", "longitude", 50),
        new ColumnLayoutData("Other Comments", "otherComments", 80),
        new ColumnLayoutData("Op Team Comments", "opteamComments", 100),
    };

    public CapControlMaintenancePendingReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlMaintenancePendingReport() {
        this(new CapControlMaintenancePendingModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
