package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlRetriesModel;

public class CapControlRetriesReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Region", "Region", 80),
        new ColumnLayoutData("SubBus", "SubBus", 80),
        new ColumnLayoutData("Feeder", "Feeder", 80),
        new ColumnLayoutData("CapBank", "CapBank", 80),
        new ColumnLayoutData("CBC", "CBC", 80),
        new ColumnLayoutData("numRetries", "Num Retries", 80),
    };

    public CapControlRetriesReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlRetriesReport() {
        this(new CapControlRetriesModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
