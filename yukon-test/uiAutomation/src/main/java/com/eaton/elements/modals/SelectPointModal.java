package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;

public class SelectPointModal extends BaseModal {
    
    private WebTable pointGroupControlDeviceTable;

    public SelectPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        pointGroupControlDeviceTable = new WebTable(driverExt, "compact-results-table", "pointGroupControlDevicePicker");
    }
    
    public WebTable getPointGroupControlDeviceTable() {
        return pointGroupControlDeviceTable;
    }
    
    public void selectPointGroupControlDeviceTable(String pointGroupName) {
        getPointGroupControlDeviceTable().searchTable(pointGroupName);        

        WebTable table = getPointGroupControlDeviceTable();
        WebTableRow row = table.getDataRowByName(pointGroupName);

        row.selectCellByLink();
    }
    
    public void selectPoint(String pointName) {
        getTable().searchTable(pointName);        

        WebTable table = getTable();
        WebTableRow row = table.getDataRowByName(pointName);

        row.selectCellByLink();
    }
}
