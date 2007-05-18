package com.cannontech.analysis.report;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.report.JFreeReportBoot;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.LabelFooterFieldFactory;
import com.cannontech.analysis.function.SumFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class LMEventReport extends SingleGroupYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Load Program", "programName", 50),
        new ColumnLayoutData("Account #", "accountNumber", 100),
        new ColumnLayoutData("First Name", "customerName", 70),
        new ColumnLayoutData("LastName", "customerName", 70),
        new ColumnLayoutData("Start Date", "startDate", 90),
        new ColumnLayoutData("Stop Date", "stopDate", 90),
        new ColumnLayoutData("Opt Out Event Count", "optOutEventCount", 40),
        new ColumnLayoutData("Opt Out Control Hours", "optOutControlHours", 40),
        new ColumnLayoutData("Control Time", "customerControlTime", 40),
    };
    
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[0], "Total"),
        new SumFooterFieldFactory(bodyColumns[4]),
    };
    
    public LMEventReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    @Override
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        return Arrays.asList(footerColumns);
    }

    @Override
    protected ColumnLayoutData getGroupFieldData() {
        return new ColumnLayoutData("Customer Name", "customerName", 140);
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
}