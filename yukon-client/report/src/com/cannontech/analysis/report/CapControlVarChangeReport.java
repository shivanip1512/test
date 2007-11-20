package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlVarChangeModel;

public class CapControlVarChangeReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Bank Name", "capBankName", 80),
        new ColumnLayoutData("Cbc Name", "cbcName", 80),
        new ColumnLayoutData("KVar Change", "kvarChange", 70),
        new ColumnLayoutData("Percent Change", "percentChange", 80),
        new ColumnLayoutData("Date/Time", "dateTime", 110),
        new ColumnLayoutData("Area", "area", 80),
        new ColumnLayoutData("Substation", "substation", 80),
        new ColumnLayoutData("Subbus", "subbus", 80),
        new ColumnLayoutData("Feeder", "feeder", 80) 
    };

    public CapControlVarChangeReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlVarChangeReport() {
        this(new CapControlVarChangeModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}


