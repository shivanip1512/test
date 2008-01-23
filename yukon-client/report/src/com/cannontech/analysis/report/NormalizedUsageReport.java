package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.NormalizedUsageModel;
import com.cannontech.simplereport.reportlayoutdata.NormalizedUsageReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class NormalizedUsageReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new NormalizedUsageReportLayoutData().getBodyColumns();

    public NormalizedUsageReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public NormalizedUsageReport() {
        this((NormalizedUsageModel)YukonSpringHook.getBean("normalizedUsageModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

