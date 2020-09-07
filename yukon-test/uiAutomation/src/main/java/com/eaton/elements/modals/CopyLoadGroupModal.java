package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CopyLoadGroupModal extends BaseModal {
    
    public CopyLoadGroupModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
}
