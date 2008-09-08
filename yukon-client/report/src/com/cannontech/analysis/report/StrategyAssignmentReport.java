package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.StrategyAssignmentModel;
import com.cannontech.simplereport.reportlayoutdata.StrategyAssignmentReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class StrategyAssignmentReport extends SingleGroupYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new StrategyAssignmentReportLayoutData().getBodyColumns();

    public StrategyAssignmentReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public StrategyAssignmentReport() {
        this((StrategyAssignmentModel)YukonSpringHook.getBean("strategyAssignmentModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Strategy", "strategyName", 200);
    }
    
}
