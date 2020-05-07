package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class SelectPointModal extends BaseModal {
    
    private WebTable table;

    public SelectPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        table = new WebTable(driverExt, "compact-results-table");
    }
    
    public WebTable getTable() {
        return table;
    }
}
