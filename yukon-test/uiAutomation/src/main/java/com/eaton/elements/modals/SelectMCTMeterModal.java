package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;

public class SelectMCTMeterModal extends BaseModal {

    public SelectMCTMeterModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
    }

    public WebTable getMctMeterTable() {
        return new WebTable(driverExt, "compact-results-table", getModal()); 
    } 
    
    public void selectMeter(String meterName) {
        getMctMeterTable().searchTable(meterName);        

        WebTable table = getMctMeterTable();
        WebTableRow row = table.getDataRowByLinkName(meterName);

        row.selectCellByLink();
    }
}