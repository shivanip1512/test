package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.BulkImportResultsModel;
import com.cannontech.simplereport.reportlayoutdata.RawPointHistoryReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class BulkImportResultsReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new RawPointHistoryReportLayoutData().getBodyColumns();

    public BulkImportResultsReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public BulkImportResultsReport() {
        this((BulkImportResultsModel)YukonSpringHook.getBean("bulkImportResultsModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}

