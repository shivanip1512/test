package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlUnsolicitedMessagesModel;

public class CapControlUnsolicitedMessagesReport  extends SimpleYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "area", 80),
        new ColumnLayoutData("SubBus", "subbus", 80),
        new ColumnLayoutData("Feeder", "feeder", 80),
        new ColumnLayoutData("Cap Bank", "capbank", 80),
        new ColumnLayoutData("CBC", "cbc", 80),
        new ColumnLayoutData("Date/Time", "datetime", 110),
        new ColumnLayoutData("Reason", "reason", 70),
        new ColumnLayoutData("State", "state", 70),
        new ColumnLayoutData("Ip Address", "address", 70)
    };

    public CapControlUnsolicitedMessagesReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlUnsolicitedMessagesReport() {
        this(new CapControlUnsolicitedMessagesModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
