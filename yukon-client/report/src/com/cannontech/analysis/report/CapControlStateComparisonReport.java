package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;

public class CapControlStateComparisonReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "region", 110),
        new ColumnLayoutData("Substation Bus", "subName", 90),
        new ColumnLayoutData("Feeder", "feederName", 60),
        new ColumnLayoutData("Cap Bank", "capBankName", 60),
        new ColumnLayoutData("CBC", "cbcName", 60),
        new ColumnLayoutData("Cap Bank Status", "capBankStatus", 60),
        new ColumnLayoutData("Cap Bank State", "capBankState", 60),
        new ColumnLayoutData("CBC Status", "cbcStatus", 60),
        new ColumnLayoutData("CBC Change Time", "cbcChangeTime", 90, columnDateFormat),
        new ColumnLayoutData("Cap Bank Change Time", "capBankChangeTime", 90, columnDateFormat),
    };

    public CapControlStateComparisonReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
