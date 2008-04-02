package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlAssetUnavailabilityModel;

public class CapControlAssetUnavailabilityReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "area", 100),
        new ColumnLayoutData("Substation", "substation", 100),
        new ColumnLayoutData("Substation Bus", "subbus", 100),
        new ColumnLayoutData("Feeder", "feeder", 100),
        new ColumnLayoutData("Substation Bus Count", "subcount", 100),
        new ColumnLayoutData("Feeder Count", "feedcount", 100)
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