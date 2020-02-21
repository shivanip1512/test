package com.eaton.elements.modals;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CopyLoadProgramModal extends BaseModal {
    
    private DriverExtensions driverExt; 
    
    public CopyLoadProgramModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        this.driverExt = driverExt;
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
    
    public DropDownElement getOperationalState() {
        return new DropDownElement(this.driverExt, "operationalState");
    }
    
    public DropDownElement getConstraint() {
        return new DropDownElement(this.driverExt, "constraint.constraintId");
    }
    
    public TrueFalseCheckboxElement getCopyMemberControl() {
        return new TrueFalseCheckboxElement(this.driverExt, "copyMemberControl");
    }
}
