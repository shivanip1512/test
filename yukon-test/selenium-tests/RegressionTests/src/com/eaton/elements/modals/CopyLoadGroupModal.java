package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CopyLoadGroupModal extends BaseModal {
    
    private DriverExtensions driverExt; 
    
    public CopyLoadGroupModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        this.driverExt = driverExt;        
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
}
