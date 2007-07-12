package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import org.jfree.report.ElementAlignment;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.MeterKwhDifferenceModel;

public class MeterKwhDifferenceReport extends SingleGroupYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Device Name", "deviceName", 200),
        new ColumnLayoutData("Meter Number", "meterNumber", 80),
        new ColumnLayoutData("Date", "date", 80, "MM/dd/yyyy"),
        new ColumnLayoutData("Time", "date", 80, "HH:mm:ss"),
        new ColumnLayoutData("kWh", "kWh", 80, "0.00#", ElementAlignment.RIGHT),
        new ColumnLayoutData("Previous kWh", "previousKwh", 80, "0.00#", ElementAlignment.RIGHT),
        new ColumnLayoutData("Difference", "difference", 80, "0.00#", ElementAlignment.RIGHT),
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

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Group", "groupName", 140);
    }

}
