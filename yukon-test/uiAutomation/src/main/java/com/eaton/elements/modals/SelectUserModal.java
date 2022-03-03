package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class SelectUserModal extends BaseModal {

    public SelectUserModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    public WebTable getTable() {
        return new WebTable(this.driverExt, "compact-results-table");
    }

}
