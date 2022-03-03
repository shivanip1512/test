package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;

import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.ExpressionFieldFactory;
import com.cannontech.analysis.function.HideDuplicateFieldFactory;
import com.cannontech.analysis.function.LabelFooterFieldFactory;
import com.cannontech.analysis.function.SumFooterFieldFactory;
import com.cannontech.analysis.function.TotalSumFooterFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class MeterUsageReport extends SingleGroupYukonReportBase
{
    private static final ColumnLayoutData groupColumn = 
    	new ColumnLayoutData("Device Name:", "deviceName", 200);
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Meter Number", "meterNumber", 200),
        new ColumnLayoutData("Device Type", "deviceType", 100),
        new ColumnLayoutData("Date", "timestamp", 60, "MM/dd/yyyy"),
        new ColumnLayoutData("Time", "timestamp", 60, "HH:mm:ss"),
        new ColumnLayoutData("Reading", "reading", 80, "0.00#", ElementAlignment.RIGHT),
        new ColumnLayoutData("Prev Reading", "previousReading", 80, "0.00#", ElementAlignment.RIGHT),
        new ColumnLayoutData("Usage", "totalUsage", 80, "0.00#", ElementAlignment.RIGHT),
    };
    
    private static final AggregateFooterFieldFactory footerColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[5], "Total"),
        new SumFooterFieldFactory(bodyColumns[6], groupColumn.getFieldName()),
    };
    
    private static final AggregateFooterFieldFactory reportFooterColumns[] = new AggregateFooterFieldFactory[] {
        new LabelFooterFieldFactory(bodyColumns[5], "Grand Total"),
        new TotalSumFooterFieldFactory(bodyColumns[6]),
    };

	private static final ExpressionFieldFactory[] bodyExpressions = new ExpressionFieldFactory[] {
			new HideDuplicateFieldFactory(bodyColumns[0]),
			new HideDuplicateFieldFactory(bodyColumns[1]),
	};
	
    public MeterUsageReport(BareReportModel bareModel) {
        super(bareModel);
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }

	@Override
	protected ColumnLayoutData getGroupFieldData() {
		return groupColumn;
	}
	
	@Override
	protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
		return Arrays.asList(footerColumns);
	}

	@Override
	protected List<? extends AggregateFooterFieldFactory> getReportFooterColumns() {
		return Arrays.asList(reportFooterColumns);
	}
	
	@Override
	protected List<? extends ExpressionFieldFactory> getBodyExpressions() {
		return Arrays.asList(bodyExpressions);
	}
}