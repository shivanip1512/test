package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;

public class SelectMCTMeterModal extends BaseModal {

    public SelectMCTMeterModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
    }

    public WebTable getuserGroupTable() {
        return new WebTable(driverExt, "compact-results-table.picker-results-table", "mctMeterPicker"); 
    } 
    
    public void selectMeter(String meterName) {
        getuserGroupTable().searchTable(meterName);        

        WebTable table = getuserGroupTable();
        WebTableRow row = table.getDataRowByName(meterName);

        row.selectCellByLink();
    }
}