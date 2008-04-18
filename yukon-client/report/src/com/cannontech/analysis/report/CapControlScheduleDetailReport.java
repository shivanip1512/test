package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlScheduleDetailModel;

public class CapControlScheduleDetailReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Schedule Name", "scheduleName", 120),
        new ColumnLayoutData("Substation Bus", "subName", 100),
        new ColumnLayoutData("Feeder", "feederName", 100),
        new ColumnLayoutData("Outgoing Command", "outgoingCommand", 120),
        new ColumnLayoutData("Last Run Time", "lastRunTime", 120),
        new ColumnLayoutData("Next Run Time", "nextRunTime", 120),
        new ColumnLayoutData("Interval", "interval", 40) 
    };

    public CapControlScheduleDetailReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlScheduleDetailReport() {
        this(new CapControlScheduleDetailModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
