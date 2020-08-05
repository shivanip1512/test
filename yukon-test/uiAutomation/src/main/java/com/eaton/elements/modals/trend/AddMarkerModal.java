package com.eaton.elements.modals.trend;

import java.util.Optional;

import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class AddMarkerModal extends BaseModal{
    
    public AddMarkerModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    public TextEditElement getValue() {
        return new TextEditElement(driverExt, "value", getModal());
    }
    
    public TextEditElement getLabel() {
        return new TextEditElement(driverExt, "label", getModal());
    }
    
    public RadioButtonElement getAxis() {
        return new RadioButtonElement(driverExt, "axis", getModal());
    }
}
