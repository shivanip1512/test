package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;

public class CapControlOperationsReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("Cbc Name", "cbcName", 50),
        new ColumnLayoutData("Op Time", "opTime", 48),
        new ColumnLayoutData("Operation", "operation", 48),
        new ColumnLayoutData("Conf Time", "confTime", 49),
        new ColumnLayoutData("Conf Status", "confStatus", 49),
        new ColumnLayoutData("Feeder Name", "feederName", 49),
        new ColumnLayoutData("Feeder ID", "feederId", 49),
        new ColumnLayoutData("Sub Name", "subName", 48),
        new ColumnLayoutData("SubBus ID", "subBusId", 49),
        new ColumnLayoutData("Area", "region", 49),
        new ColumnLayoutData("Bank Size", "bankSize", 49),
        new ColumnLayoutData("Protocol", "protocol", 49),
        new ColumnLayoutData("IP Address", "ipAddress", 45),
        new ColumnLayoutData("Serial Num", "serialNum", 49),
        new ColumnLayoutData("Slave Address", "slaveAddress", 50),
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
