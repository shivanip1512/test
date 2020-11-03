package com.eaton.elements.modals.attributes;

import java.util.Optional;

import com.eaton.elements.DropDownMultiSelectElement;
import com.eaton.framework.DriverExtensions;

public class AddAttributeAssignmentsModal  extends AttributeAssignmentsModal {
    
    private DropDownMultiSelectElement deviceType;

    public AddAttributeAssignmentsModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        deviceType = new DropDownMultiSelectElement(driverExt, "deviceTypes", getModal());
    }
    
    public DropDownMultiSelectElement getDeviceType() {
        return deviceType;
    }
}
