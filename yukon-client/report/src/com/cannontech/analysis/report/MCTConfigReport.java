package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.MCTConfigModel;
import com.cannontech.spring.YukonSpringHook;

public class MCTConfigReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("MCT Name", "mctName", 100),
            new ColumnLayoutData("Type", "type", 100),
            new ColumnLayoutData("Last Intrvl Demand Rate (min)", "demandRate", 100),
            new ColumnLayoutData("Load Profile Demand Rate (min)", "profileRate", 100),
            new ColumnLayoutData("Config Name", "configName", 100),
            new ColumnLayoutData("Config Intrvl Demand Rate (min)", "configDemandRate", 100),
            new ColumnLayoutData("Config Load Profile Demand Rate (min)", "configProfileRate", 100)
        };

    public MCTConfigReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public MCTConfigReport() {
        this(YukonSpringHook.getBean("mctConfigModel", MCTConfigModel.class));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
