package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.MeterKwhDifferenceModel;

public class MeterKwhDifferenceReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Device Name", "deviceName", 200),
        new ColumnLayoutData("Meter Number", "meterNumber", 80),
        new ColumnLayoutData("Date", "date", 80),
        new ColumnLayoutData("kWh", "kWh", 80),
        new ColumnLayoutData("Previous kWh", "previousKwh", 80),
        new ColumnLayoutData("Difference", "difference", 80),
    };

    public MeterKwhDifferenceReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public MeterKwhDifferenceReport() {
        this(new MeterKwhDifferenceModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
