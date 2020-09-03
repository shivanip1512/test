package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CopyLoadProgramModal extends BaseModal {
    
    private WebElement modal;
    
    public CopyLoadProgramModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        this.driverExt = driverExt;
        modal = getModal();
    }
    
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name", modal);
    }
    
    public DropDownElement getOperationalState() {
        return new DropDownElement(this.driverExt, "operationalState", modal);
    }
    
    public DropDownElement getConstraint() {
        return new DropDownElement(this.driverExt, "constraint.constraintId", modal);
    }
    
    public TrueFalseCheckboxElement getCopyMemberControl() {
        return new TrueFalseCheckboxElement(this.driverExt, "copyMemberControl", modal);
    }
}
