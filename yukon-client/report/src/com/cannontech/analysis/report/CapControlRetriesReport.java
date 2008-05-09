package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlRetriesModel;

public class CapControlRetriesReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "Region", 120),
        new ColumnLayoutData("Substation Bus", "SubBus", 110),
        new ColumnLayoutData("Feeder", "Feeder", 110),
        new ColumnLayoutData("Cap Bank", "CapBank", 110),
        new ColumnLayoutData("CBC", "CBC", 110),
        new ColumnLayoutData("Retries", "numRetries", 50),
        new ColumnLayoutData("Attempts", "numAttempts", 50),
        new ColumnLayoutData("Success %", "successPercent", 50),
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
