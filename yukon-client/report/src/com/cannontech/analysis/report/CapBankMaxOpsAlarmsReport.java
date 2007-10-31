package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapBankMaxOpsAlarmsModel;

public class CapBankMaxOpsAlarmsReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("CapBank Name", "capBankName", 80),
        new ColumnLayoutData("Feeder Name", "feederName", 80),
        new ColumnLayoutData("Max Daily Ops", "maxDailyOps", 80)
    };

    public CapBankMaxOpsAlarmsReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapBankMaxOpsAlarmsReport() {
        this(new CapBankMaxOpsAlarmsModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}