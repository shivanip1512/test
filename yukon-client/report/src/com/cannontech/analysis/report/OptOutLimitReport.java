package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.google.common.collect.Lists;

public class OptOutLimitReport extends GroupYukonReportBase {
    
    public OptOutLimitReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Alternate Tracking Number", "alternateTrackingNumber", 130),
        new ColumnLayoutData("Enrolled Program", "enrolledProgram", 240),
        new ColumnLayoutData("Overrides Used", "numberOfOverridesUsed", 80),
        new ColumnLayoutData("Username", "issuingUser", 80),
        new ColumnLayoutData("Start Date", "optOutStart", 100),
        new ColumnLayoutData("Stop Date", "optOutStop", 100),
    };
    
    private static final ColumnLayoutData groupColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Account: ", "accountNumberAndName", 300),
        new ColumnLayoutData("Serial Number: ", "serialNumber", 200)
    };
    
    @Override
    protected List<ColumnLayoutData> getMultiGroupFieldData() {
        List<ColumnLayoutData> groupFieldData = Lists.newArrayList(groupColumns);
        return groupFieldData;
    }
    
    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
}