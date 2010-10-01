package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class ProgramAndGearControlReport extends SingleGroupYukonReportBase {
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Start Date", "startDate", 120),
        new ColumnLayoutData("Gear Name", "gearName", 120),
    };
    
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
    };
    
    public ProgramAndGearControlReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Program Name: ", "programName", 150);
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

}
