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
        new ColumnLayoutData("SubName", "SubName", 60),
        new ColumnLayoutData("FeederName", "FeederName", 60),
        new ColumnLayoutData("BankName", "BankName", 60),
        new ColumnLayoutData("CBCName", "CBCName", 60),
        new ColumnLayoutData("Attempts", "Attempts", 40),
        new ColumnLayoutData("Success", "Success", 40),
        new ColumnLayoutData("Questionable", "Questionable", 60),
        new ColumnLayoutData("Failure", "Failure", 40),
        new ColumnLayoutData("SuccessPcnt", "SuccessPcnt", 55),
        new ColumnLayoutData("Protocol", "Protocol", 50),
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
