package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class EditMeterModal extends BaseModal {

    public EditMeterModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    //TODO need to add all fields as there are more based on the device you select
    
    public TextEditElement getdeviceName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
    
    public TextEditElement getMeterNumber() {
        return new TextEditElement(this.driverExt, "meterNumber", getModal());
    }
    
    public TextEditElement getPhycialAddress() {
        return  new TextEditElement(this.driverExt, "address", getModal());
    }
    
    public DropDownElement getRoute() {
        return new DropDownElement(this.driverExt, "routeId", getModal());
    }
    
    public TextEditElement getSerialNumber() {
        return new TextEditElement(this.driverExt, "serialNumber", getModal());
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled", getModal());
    }
}
