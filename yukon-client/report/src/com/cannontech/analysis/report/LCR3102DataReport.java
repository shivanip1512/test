package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.LCR3102DataModel;
import com.cannontech.simplereport.reportlayoutdata.LCR3102DataReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class LCR3102DataReport extends SingleGroupYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new LCR3102DataReportLayoutData().getBodyColumns();

    public LCR3102DataReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public LCR3102DataReport() {
        this((LCR3102DataModel)YukonSpringHook.getBean("lcr3102DataModel"));
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
