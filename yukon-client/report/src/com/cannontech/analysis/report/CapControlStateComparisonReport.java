package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlStateComparisonModel;

public class CapControlStateComparisonReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "region", 170),
        new ColumnLayoutData("Substation Bus", "subName", 90),
        new ColumnLayoutData("Feeder", "feederName", 60),
        new ColumnLayoutData("Cap Bank", "capBankName", 60),
        new ColumnLayoutData("CBC", "cbcName", 60),
        new ColumnLayoutData("Cap Bank Status", "capBankStatus", 60),
        new ColumnLayoutData("CBC Status", "cbcStatus", 60),
        new ColumnLayoutData("CBC Change Time", "cbcChangeTime", 90),
        new ColumnLayoutData("Cap Bank Change Time", "capBankChangeTime", 90),
    };

    public CapControlStateComparisonReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlStateComparisonReport() {
        this(new CapControlStateComparisonModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
