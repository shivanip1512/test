package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;

public class CapControlConfirmationPercentageReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Area", "Region", 60),
        new ColumnLayoutData("OpCenter", "OpCenter", 60),
        new ColumnLayoutData("TA", "TA", 40),
        new ColumnLayoutData("Substation Bus", "SubName", 60),
        new ColumnLayoutData("Feeder", "FeederName", 60),
        new ColumnLayoutData("Cap Bank", "BankName", 60),
        new ColumnLayoutData("CBC", "CBCName", 60),
        new ColumnLayoutData("Attempts", "Attempts", 40),
        new ColumnLayoutData("Success", "Success", 40),
        new ColumnLayoutData("Questionable", "Questionable", 60),
        new ColumnLayoutData("Failure", "Failure", 40),
        new ColumnLayoutData("SuccessPcnt", "SuccessPcnt", 55),
        new ColumnLayoutData("Refusals", "Refusals", 50),
    };

    public CapControlConfirmationPercentageReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlConfirmationPercentageReport() {
        this(new CapControlOperationsModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
