package com.eaton.elements.modals;

import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CopyLoadGroupModal extends BaseModal {
    
    private DriverExtensions driverExt; 
    
    public CopyLoadGroupModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        this.driverExt = driverExt;        
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
}
