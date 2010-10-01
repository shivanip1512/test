package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlDisabledDevicesModel;
import com.cannontech.simplereport.reportlayoutdata.CapControlDisabledDevicesReportLayoutData;
import com.cannontech.spring.YukonSpringHook;

public class CapControlDisabledDevicesReport extends GroupYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new CapControlDisabledDevicesReportLayoutData().getBodyColumns();

    public CapControlDisabledDevicesReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlDisabledDevicesReport() {
        this((CapControlDisabledDevicesModel)YukonSpringHook.getBean("capControlDisabledDevicesModel"));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Device Type", "deviceType", 200);
    }
    
}
