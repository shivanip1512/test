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
    
//  The total footers are being removed for now due to how we are calculating totals.
//  We may want to change this in the future to just total up a given inventory's opt outs.
//    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
//        new LabelFooterFieldFactory(bodyColumns[0], "Total"),
//        new SumFooterFieldFactory(bodyColumns[2]),
//    };
    
//    @Override
//    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
//        return Arrays.asList(footerColumns);
//    }
    
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
