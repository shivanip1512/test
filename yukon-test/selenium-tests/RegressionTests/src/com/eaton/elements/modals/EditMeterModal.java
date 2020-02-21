package com.eaton.elements.modals;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class EditMeterModal extends BaseModal {

    private DriverExtensions driverExt;
    
    public EditMeterModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        this.driverExt = driverExt;
    }
    
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
