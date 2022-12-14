package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CBCSpecialAreaAssignmentModel;

public class CBCSpecialAreaAssignmentReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Special Area", "specialArea", 100),
        new ColumnLayoutData("Area", "area", 100),
        new ColumnLayoutData("Substation", "substation", 100),
        new ColumnLayoutData("Substation Bus", "substationBus", 100),
        new ColumnLayoutData("Feeder", "feeder", 100),
        new ColumnLayoutData("Cap Bank", "capBank", 100),
        new ColumnLayoutData("CBC", "cbc", 100)
    };

    public CBCSpecialAreaAssignmentReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CBCSpecialAreaAssignmentReport() {
        this(new CBCSpecialAreaAssignmentModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
