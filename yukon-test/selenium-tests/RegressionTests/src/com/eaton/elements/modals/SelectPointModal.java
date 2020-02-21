package com.eaton.elements.modals;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class SelectPointModal extends BaseModal {
    
    private WebTable table;

    public SelectPointModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        table = new WebTable(driverExt, "compact-results-table");
    }
    
    public WebTable getTable() {
        return table;
    }
}
