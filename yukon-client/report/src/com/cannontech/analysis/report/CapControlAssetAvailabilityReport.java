package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlAssetAvailabilityModel;

public class CapControlAssetAvailabilityReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "area", 100),
        new ColumnLayoutData("Substation", "substation", 100),
        new ColumnLayoutData("Subbus", "subbus", 100),
        new ColumnLayoutData("Feeder", "feeder", 100),
        new ColumnLayoutData("Sub Count", "subcount", 100),
        new ColumnLayoutData("Feeder Count", "feedcount", 100)
    };

    public CapControlAssetAvailabilityReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlAssetAvailabilityReport() {
        this(new CapControlAssetAvailabilityModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
}