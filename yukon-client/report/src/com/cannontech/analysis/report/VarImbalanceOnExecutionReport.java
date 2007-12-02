package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.VarImbalanceOnExecutionModel;
import com.cannontech.simplereport.reportlayoutdata.VarImbalanceOnExecutionReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class VarImbalanceOnExecutionReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new VarImbalanceOnExecutionReportLayoutData().getBodyColumns();

    public VarImbalanceOnExecutionReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public VarImbalanceOnExecutionReport() {
        this((VarImbalanceOnExecutionModel)YukonSpringHook.getBean("varImbalanceOnExecutionModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}