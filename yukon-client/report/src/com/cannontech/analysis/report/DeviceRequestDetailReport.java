package com.cannontech.analysis.report;

import java.util.Arrays;
import java.util.List;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.DeviceRequestDetailModel;
import com.cannontech.spring.YukonSpringHook;

public class DeviceRequestDetailReport extends SimpleYukonReportBase {
    
    DeviceRequestDetailModel deviceRequestDetailModel;
    
    public DeviceRequestDetailReport(BareReportModel bareModel) {
        super(bareModel);
        this.deviceRequestDetailModel = (DeviceRequestDetailModel)bareModel;
    }
    
    public DeviceRequestDetailReport() {
        this(YukonSpringHook.getBean("deviceRequestDetailModel", DeviceRequestDetailModel.class));
    }

    @Override
    protected List<ColumnLayoutData> getBodyColumns() {
        ColumnLayoutData[] bodyColumns = new ColumnLayoutData[] {
            new ColumnLayoutData("Device Name", "deviceName", 150),
            new ColumnLayoutData("Device Type", "type", 100),
            new ColumnLayoutData("Route", "route", 150),
            new ColumnLayoutData(deviceRequestDetailModel.isLifetime() ? "Lifetime Requests" : "Requests", "requests", 150),
            new ColumnLayoutData(deviceRequestDetailModel.isLifetime() ? "Lifetime Attempts Per Success" : "Attempts Per Success", "success", 150),
        };
        return Arrays.asList(bodyColumns);
    }

}
