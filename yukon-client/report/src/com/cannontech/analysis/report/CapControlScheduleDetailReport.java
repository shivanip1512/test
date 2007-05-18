package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlScheduleDetailModel;

public class CapControlScheduleDetailReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Schedule Name", "scheduleName", 120),
        new ColumnLayoutData("Sub Name", "subName", 80),
        new ColumnLayoutData("Feeder Name", "feederName", 80),
        new ColumnLayoutData("Outgoing Command", "outgoingCommand", 120),
        new ColumnLayoutData("Last Run Time", "lastRunTime", 120),
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
