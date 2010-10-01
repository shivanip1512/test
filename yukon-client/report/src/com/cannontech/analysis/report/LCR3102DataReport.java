package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.simplereport.reportlayoutdata.LCR3102DataReportLayoutData;

public class LCR3102DataReport extends GroupYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new LCR3102DataReportLayoutData().getBodyColumns();

    public LCR3102DataReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Device Name", "deviceName", 200);
    }
    
}
