package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.OptOutInfoModel;
import com.cannontech.spring.YukonSpringHook;

public class OptOutInfoReport extends SingleGroupYukonReportBase {
    
    public OptOutInfoReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public OptOutInfoReport() {
        this(YukonSpringHook.getBean("optOutInfoModel", OptOutInfoModel.class));
    }

    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Serial Number", "serialNumber", 60),
        new ColumnLayoutData("Enrolled Program", "enrolledProgram", 100),
        new ColumnLayoutData("Control Start Time", "startTimeOfControl", 80),
        new ColumnLayoutData("Control Stop Time", "stopTimeOfControl", 80),
        new ColumnLayoutData("Projected Control Hours", "totalProjectedControlHoursForPeriod", 45),
        new ColumnLayoutData("Actual Control Hours", "totalActualControlHoursForPeriod", 45),
        new ColumnLayoutData("Date Scheduled", "dateOverrideWasScheduled", 80),
        new ColumnLayoutData("Start Time of Override", "startTimeOfOverride", 80),
        new ColumnLayoutData("Stop Time of Override", "stopTimeOfOverride", 80),
        new ColumnLayoutData("Total Override Hours", "totalOverrideHours", 45),
        new ColumnLayoutData("Counted Toward Limit", "countedTowardOptOutLimit", 50),
    };
    
    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Account: ", "accountNumberAndName", 220);
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
