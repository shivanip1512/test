package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;

public class SelectPointModal extends BaseModal {
    
    public SelectPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);       
    }
    
    public WebTable getPointTable() {
        return new WebTable(driverExt, "compact-results-table", getModal());
    }
    

    /**
     * @param pointName = pass in the name of the point
     * @param id = optional field, use if name is not unique, otherwise pass in Optional.empty()
     */
    public void selectPoint(String pointName, Optional<String> id) {
        if(id.isPresent()) {
            getPointTable().searchTable(id.get());
        } else {
            getPointTable().searchTable(pointName);    
        }                

        WebTable table = getPointTable();
        WebTableRow row = table.getDataRowByName(pointName);

        row.selectCellByLink();
    }
}
