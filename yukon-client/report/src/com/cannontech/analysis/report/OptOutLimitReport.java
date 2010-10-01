package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.LabelFooterFieldFactory;
import com.cannontech.analysis.function.SumFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.OptOutLimitModel;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class OptOutLimitReport extends GroupYukonReportBase {
    
    public OptOutLimitReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public OptOutLimitReport() {
        this(YukonSpringHook.getBean("optOutLimitModel", OptOutLimitModel.class));
    }

    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Alternate Tracking Number", "alternateTrackingNumber", 130),
        new ColumnLayoutData("Enrolled Program", "enrolledProgram", 220),
        new ColumnLayoutData("Overrides Used", "numberOfOverridesUsed", 120),
        new ColumnLayoutData("Username", "issuingUser", 80),
        new ColumnLayoutData("Start Date", "optOutStart", 100),
        new ColumnLayoutData("Stop Date", "optOutStop", 100),
    };
    
    private static final ColumnLayoutData groupColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Account: ", "accountNumberAndName", 300),
        new ColumnLayoutData("Serial Number: ", "serialNumber", 200)
    };
    
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[0], "Total"),
        new SumFooterFieldFactory(bodyColumns[2]),
    };
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }
    
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
