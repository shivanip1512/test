package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlAssetUnavailabilityModel;

public class CapControlAssetUnavailabilityReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "Area", 100),
        new ColumnLayoutData("Substation", "Substation", 100),
        new ColumnLayoutData("Bus", "Bus", 100),
        new ColumnLayoutData("Feeder", "Feeder", 100),
        new ColumnLayoutData("Bus Unable \nto Close", "BusUTC", 75),
        new ColumnLayoutData("Feeder Unable \nto Close", "FeederUTC", 75),
        new ColumnLayoutData("Bus Unable \nto Open", "BusUTO", 75),
        new ColumnLayoutData("Feeder Unable \nto Open", "FeederUTO", 75)
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