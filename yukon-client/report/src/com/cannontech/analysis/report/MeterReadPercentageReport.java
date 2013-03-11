package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.MeterReadPercentageModel;
import com.cannontech.spring.YukonSpringHook;

public class MeterReadPercentageReport extends SimpleYukonReportBase{
    MeterReadPercentageModel meterReadPercentageModel;
    
    public MeterReadPercentageReport(BareReportModel bareModel) {
        super(bareModel);
        this.meterReadPercentageModel= (MeterReadPercentageModel)bareModel;
    }
    
    public MeterReadPercentageReport() {
        this(YukonSpringHook.getBean("meterReadPercentageModel", MeterReadPercentageModel.class));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Group Name", "groupName", 174),
            new ColumnLayoutData("Start Date", "startDate", 62, "MM/dd/yyyy"),
            new ColumnLayoutData("End Date", "endDate", 62, "MM/dd/yyyy"),
            new ColumnLayoutData("Reading %", "readPercent", 62, "###.##%"),
            new ColumnLayoutData("Successful", "countSuccessful", 62),
            new ColumnLayoutData("Missed", "countMissed", 62),
            new ColumnLayoutData("Total", "countTotal", 62),
            new ColumnLayoutData("Disabled", "countDisabled", 62),
            new ColumnLayoutData("Unsupported", "countUnsupported", 62),
            new ColumnLayoutData("No Data", "countNoData", 62),
        };
        return Arrays.asList(bodyColumns);
    }
}
