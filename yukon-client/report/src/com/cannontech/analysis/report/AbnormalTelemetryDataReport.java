package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.AbnormalTelemetryDataModel;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class AbnormalTelemetryDataReport extends SimpleYukonReportBase {
    
private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        
        new ColumnLayoutData("Device", "device", 110),
        new ColumnLayoutData("Type", "type", 110),
        new ColumnLayoutData("Point", "point", 110),
        new ColumnLayoutData("Units", "units", 110),
        new ColumnLayoutData("Quality", "quality", 110),
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
