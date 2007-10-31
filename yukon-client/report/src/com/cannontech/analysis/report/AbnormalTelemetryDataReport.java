package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.AbnormalTelemetryDataModel;

public class AbnormalTelemetryDataReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("SubstationBus", "substationBus", 80),
        new ColumnLayoutData("Sub Var Point", "subVarPoint", 100),
        new ColumnLayoutData("Sub Volt Point", "subVoltPoint", 100),
        new ColumnLayoutData("Sub Watt Point", "subWattPoint", 100),
        new ColumnLayoutData("Feeder", "feederName", 80),
        new ColumnLayoutData("Var Point", "varPoint", 100),
        new ColumnLayoutData("Volt Point", "voltPoint", 100),
        new ColumnLayoutData("Watt Point", "wattPoint", 100)
    };

    public AbnormalTelemetryDataReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public AbnormalTelemetryDataReport() {
        this(new AbnormalTelemetryDataModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
