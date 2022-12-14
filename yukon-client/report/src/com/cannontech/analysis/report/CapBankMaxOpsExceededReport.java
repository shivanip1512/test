package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapBankMaxOpsExceededModel;

public class CapBankMaxOpsExceededReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        
    	new ColumnLayoutData("Area", "areaName", 120),
        new ColumnLayoutData("Substation", "substationName", 120),
        new ColumnLayoutData("Substation Bus", "subBusName", 120),
        new ColumnLayoutData("Feeder", "feederName", 120),
    	new ColumnLayoutData("Cap Bank", "capBankName", 120),
        new ColumnLayoutData("Date/Time", "dateTime", 120, columnDateFormat)
    };

    public CapBankMaxOpsExceededReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapBankMaxOpsExceededReport() {
        this(new CapBankMaxOpsExceededModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
}
