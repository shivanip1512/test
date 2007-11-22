package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.RawPointHistoryModel;
import com.cannontech.simplereport.reportlayoutdata.RawPointHistoryReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class RawPointHistoryReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new RawPointHistoryReportLayoutData().getBodyColumns();

    public RawPointHistoryReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public RawPointHistoryReport() {
        this((RawPointHistoryModel)YukonSpringHook.getBean("rawPointHistoryModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

