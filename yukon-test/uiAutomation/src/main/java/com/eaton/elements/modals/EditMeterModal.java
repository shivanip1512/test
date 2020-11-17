package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class EditMeterModal extends BaseModal {

    public EditMeterModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    //TODO need to add all fields as there are more based on the device you select
    
    public TextEditElement getDeviceName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }
    
    public TextEditElement getMeterNumber() {
        return new TextEditElement(this.driverExt, "meterNumber", getModal());
    }
    
    public TextEditElement getPhysicalAddress() {
        return  new TextEditElement(this.driverExt, "address", getModal());
    }
    
    public DropDownElement getRoute() {
        return new DropDownElement(this.driverExt, "routeId", getModal());
    }
    
    public TextEditElement getSerialNumber() {
        return new TextEditElement(this.driverExt, "serialNumber", getModal());
    }
    
    public TextEditElement getManufacturer() {
        return new TextEditElement(this.driverExt, "manufacturer", getModal());
    }
    
    public TextEditElement getModel() {
        return new TextEditElement(this.driverExt, "model", getModal());
    }
    
    public SwitchBtnYesNoElement getStatus() {
        return new SwitchBtnYesNoElement(this.driverExt, "disabled", getModal());
    }
}
