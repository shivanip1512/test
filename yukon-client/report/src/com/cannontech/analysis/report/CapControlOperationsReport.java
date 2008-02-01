package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.CapControlOperationsModel;

public class CapControlOperationsReport extends SimpleYukonReportBase {
    
    private static final ColumnLayoutData bodyColumns[] = new ColumnLayoutData[] {
        new ColumnLayoutData("CBC", "cbcName", 45),
        new ColumnLayoutData("Operation Time", "opTime", 45),
        new ColumnLayoutData("Operation", "operation", 45),
        new ColumnLayoutData("Conf Time", "confTime", 45),
        new ColumnLayoutData("Conf Status", "confStatus", 45),
        new ColumnLayoutData("Status Quality", "bankStatusQuality", 45),
        new ColumnLayoutData("Feeder", "feederName", 45),
        new ColumnLayoutData("Feeder ID", "feederId", 45),
        new ColumnLayoutData("Substation Bus", "subName", 45),
        new ColumnLayoutData("Substation Bus ID", "subBusId", 45),
        new ColumnLayoutData("Area", "region", 45),
        new ColumnLayoutData("Cap Bank Size", "bankSize", 45),
        new ColumnLayoutData("Protocol", "protocol", 45),
        new ColumnLayoutData("IP Address", "ipAddress", 45),
        new ColumnLayoutData("Serial Num", "serialNum", 45),
        new ColumnLayoutData("Slave Address", "slaveAddress", 45),
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
