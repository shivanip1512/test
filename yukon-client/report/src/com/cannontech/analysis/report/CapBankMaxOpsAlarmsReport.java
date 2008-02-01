package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapBankMaxOpsAlarmsModel;

public class CapBankMaxOpsAlarmsReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        
    	new ColumnLayoutData("Area Name", "areaName", 80),
        new ColumnLayoutData("Substation Name", "substationName", 80),
        new ColumnLayoutData("Substation Bus Name", "subBusName", 120),
        new ColumnLayoutData("Feeder", "feederName", 80),
    	new ColumnLayoutData("Cap Bank", "capBankName", 80),
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