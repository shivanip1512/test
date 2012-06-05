package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.DeviceReadingsModel;
import com.cannontech.spring.YukonSpringHook;

public class DeviceReadingsReport extends SimpleYukonReportBase {
    
    DeviceReadingsModel deviceReadingsModel;
    
    public DeviceReadingsReport(BareReportModel bareModel) {
        super(bareModel);
        this.deviceReadingsModel = (DeviceReadingsModel)bareModel;
    }
    
    public DeviceReadingsReport() {
        this(YukonSpringHook.getBean("deviceReadingsModel", DeviceReadingsModel.class));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Device Name", "deviceName", 175),
            new ColumnLayoutData("Type", "type", 125),
            new ColumnLayoutData("Date", "date", 125),
            new ColumnLayoutData(deviceReadingsModel.getAttribute().getMessage().getDefaultMessage(), "value", 125),
        };
        return Arrays.asList(bodyColumns);
    }

}
