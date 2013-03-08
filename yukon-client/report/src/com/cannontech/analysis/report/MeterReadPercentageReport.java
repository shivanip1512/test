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
            new ColumnLayoutData("Group Name", "groupName", 102),
            new ColumnLayoutData("Start Date", "startDate", 70, "MM/dd/yyyy"),
            new ColumnLayoutData("End Date", "endDate", 70, "MM/dd/yyyy"),
            new ColumnLayoutData("Reading %", "readPercent", 70, "###.##%"),
            new ColumnLayoutData("Successful", "countSuccessful", 70),
            new ColumnLayoutData("Missed", "countMissed",70),
            new ColumnLayoutData("Total", "countTotal", 70),
            new ColumnLayoutData("Disabled", "countDisabled", 70),
            new ColumnLayoutData("Unsupported", "countUnsupported", 70),
            new ColumnLayoutData("No Data", "countNoData", 70),
        };
        return Arrays.asList(bodyColumns);
    }
}
