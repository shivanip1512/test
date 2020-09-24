package com.eaton.elements.modals.attributes;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.framework.DriverExtensions;

public class EditAttributeAssignmentsModal extends AttributeAssignmentsModal {
    
    private DropDownElement deviceType;

    public EditAttributeAssignmentsModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        deviceType = new DropDownElement(driverExt, "paoType", getModal());
    }

    public DropDownElement getDeviceType() {
        return deviceType;
    }
}
