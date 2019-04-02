package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlAssetUnavailabilityModel;

public class CapControlAssetUnavailabilityReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "area", 100),
        new ColumnLayoutData("Substation", "substation", 100),
        new ColumnLayoutData("Bus", "bus", 100),
        new ColumnLayoutData("Feeder", "feeder", 100),
        new ColumnLayoutData("Bus Unable \nto Close", "busUtc", 75),
        new ColumnLayoutData("Feeder Unable \nto Close", "feederUtc", 75),
        new ColumnLayoutData("Bus Unable \nto Open", "busUto", 75),
        new ColumnLayoutData("Feeder Unable \nto Open", "feederUto", 75)
    };

    public CapControlAssetUnavailabilityReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlAssetUnavailabilityReport() {
        this(new CapControlAssetUnavailabilityModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
}