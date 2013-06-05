package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.function.ExpressionFieldFactory;
import com.cannontech.analysis.function.HideDuplicateFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;

public class DisconnectReport extends SimpleYukonReportBase
{
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Device Name", "deviceName", 150),
        new ColumnLayoutData("Meter #", "meterNumber", 65),
        new ColumnLayoutData("Address/Serial#", "address", 70),
        new ColumnLayoutData("Type", "deviceType", 70),
        new ColumnLayoutData("Route Name", "routeName", 100),
        new ColumnLayoutData("Collar Addr", "discAddress", 60, "####"),
        new ColumnLayoutData("Timestamp", "timestamp", 90, "MM/dd/yyyy HH:mm:ss"),
        new ColumnLayoutData("State", "state", 65),
    };
    
	private static final ExpressionFieldFactory[] bodyExpressions = new ExpressionFieldFactory[] {
			new HideDuplicateFieldFactory(bodyColumns[0]),
			new HideDuplicateFieldFactory(bodyColumns[1]),
			new HideDuplicateFieldFactory(bodyColumns[2]),
			new HideDuplicateFieldFactory(bodyColumns[3]),
			new HideDuplicateFieldFactory(bodyColumns[4]),
			new HideDuplicateFieldFactory(bodyColumns[5]),
	};
	
    public DisconnectReport(BareReportModel bareModel) {
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