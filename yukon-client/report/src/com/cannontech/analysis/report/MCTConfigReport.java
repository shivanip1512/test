package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.MCTConfigModel;
import com.cannontech.spring.YukonSpringHook;

public class MCTConfigReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Meter Name", "mctName", 175),
            new ColumnLayoutData("Type", "type", 75),
            new ColumnLayoutData("Demand\r\nInterval", "demandRate", 75),
            new ColumnLayoutData("LP Demand\r\nRate (min)", "profileRate", 75),
            new ColumnLayoutData("Config Name", "configName", 150),
            new ColumnLayoutData("Config Demand\r\nInterval", "configDemandRate", 80),
            new ColumnLayoutData("Config LP\r\nDemand Rate (min)", "configProfileRate", 90)
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
