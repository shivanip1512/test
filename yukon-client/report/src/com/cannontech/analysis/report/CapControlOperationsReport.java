package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;

public class CapControlOperationsReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("CBC", "cbcName", 45),
        new ColumnLayoutData("Operation Time", "opTime", 65),
        new ColumnLayoutData("Operation", "operation", 115),
        new ColumnLayoutData("Conf Time", "confTime", 65, columnDateFormat),
        new ColumnLayoutData("Conf Status", "confStatus", 110, columnDateFormat),
        new ColumnLayoutData("Quality", "bankStatusQuality", 75),
        new ColumnLayoutData("Feeder", "feederName", 45),
        new ColumnLayoutData("Sub Bus", "subName", 60),
        new ColumnLayoutData("Bank Size", "bankSize", 35),
        new ColumnLayoutData("IP Address", "ipAddress", 50),
        new ColumnLayoutData("Serial#", "serialNum", 45),
        new ColumnLayoutData("Slave", "slaveAddress", 25),
    };

    public CapControlOperationsReport(BareReportModel bareModel) {
        super(bareModel);
    }
    
    public CapControlOperationsReport() {
        this(new CapControlOperationsModel());
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        return Arrays.asList(bodyColumns);
    }
    
    @Override
    protected void applyLabelProperties(LabelElementFactory labelFactory, ColumnLayoutData layoutData) {
        super.applyLabelProperties(labelFactory, layoutData);
        labelFactory.setFontSize(8);
    }
  
    @Override
    protected void applyFieldProperties(TextFieldElementFactory fieldFactory, ColumnLayoutData layoutData) {
        super.applyFieldProperties(fieldFactory, layoutData);
        fieldFactory.setFontSize(8);
    }

}
