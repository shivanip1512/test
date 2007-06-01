package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;

public class CapBankOperationsPerformanceReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Bank Name", "bankName", 80),
        new ColumnLayoutData("Cbc Name", "cbcName", 80),
        new ColumnLayoutData("Feeder Name", "feederName", 80),
        new ColumnLayoutData("Sub Name", "subName", 80),
        new ColumnLayoutData("Area", "region", 180),
        new ColumnLayoutData("Status", "text", 80),
        new ColumnLayoutData("Status Count", "qCount", 40),
        new ColumnLayoutData("Total Count", "totCount", 40),
        new ColumnLayoutData("Percent", "qPercent", 40).setFormat("#0.0#"), 
    };

    public CapBankOperationsPerformanceReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapBankOperationsPerformanceReport() {
        this(new CapControlOperationsModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

