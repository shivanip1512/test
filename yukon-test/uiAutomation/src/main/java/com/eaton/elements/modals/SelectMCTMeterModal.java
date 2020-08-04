package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;

public class SelectMCTMeterModal extends BaseModal {

private WebTable userGroupTable;
    
    public SelectMCTMeterModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        userGroupTable = new WebTable(driverExt, "compact-results-table.picker-results-table", "mctMeterPicker");  
    }

    public WebTable getuserGroupTable() {
        return userGroupTable;
    } 
    
    public void selectMeter(String meterName) {
        getuserGroupTable().searchTable(meterName);        

        WebTable table = getuserGroupTable();
        WebTableRow row = table.getDataRowByName(meterName);

        row.selectCellByLink();
    }
}