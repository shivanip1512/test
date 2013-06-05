package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.function.ExpressionFieldFactory;
import com.cannontech.analysis.function.HideDuplicateFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class MeterOutageReport extends SimpleYukonReportBase
{
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Device Name", "deviceName", 180),
        new ColumnLayoutData("Meter #", "meterNumber", 80),
        new ColumnLayoutData("Address/Serial#", "address", 80),
        new ColumnLayoutData("Type", "deviceType", 80),
        new ColumnLayoutData("Route Name", "routeName", 120),
        new ColumnLayoutData("Date/Time", "timestamp", 100, "MM/dd/yyyy HH:mm:ss"),
        new ColumnLayoutData("Duration", "duration", 80),
    };

	private static final ExpressionFieldFactory[] bodyExpressions = new ExpressionFieldFactory[] {
			new HideDuplicateFieldFactory(bodyColumns[0]),
			new HideDuplicateFieldFactory(bodyColumns[1]),
			new HideDuplicateFieldFactory(bodyColumns[2]),
			new HideDuplicateFieldFactory(bodyColumns[3]),
			new HideDuplicateFieldFactory(bodyColumns[4]),
	};
	
    public MeterOutageReport(BareReportModel bareModel) {
        super(bareModel);
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
	
	@Override
	protected List<? extends ExpressionFieldFactory> getBodyExpressions() {
		return Arrays.asList(bodyExpressions);
	}
}