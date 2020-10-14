package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.DropDownSearchElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class CreateMeterModal extends BaseModal {

    public CreateMeterModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        this.driverExt = driverExt;
    }

    private DriverExtensions driverExt;
    private static final String PARENT_NAME = "contentPopup";
    
    public DropDownSearchElement getType() {
        return new DropDownSearchElement(this.driverExt, "meter_type_chosen", PARENT_NAME);
    }
    
    public TextEditElement getDeviceName() {
        return  new TextEditElement(this.driverExt, "name", PARENT_NAME);
    }
    
    public TextEditElement getMeterNumber() {
        return new TextEditElement(this.driverExt, "meterNumber", PARENT_NAME);
    }
    
    public TextEditElement getPhysicalAddress() {
        return new TextEditElement(this.driverExt, "address", PARENT_NAME);
    }
    
    public DropDownElement getRoute() {
        return new DropDownElement(this.driverExt, "routeId", PARENT_NAME);
    }
    
    public TextEditElement getSerialNumber() {
        return new TextEditElement(this.driverExt, "serialNumber", PARENT_NAME);
    }
    
    public TextEditElement getManufacturer() {
        return new TextEditElement(this.driverExt, "manufacturer", PARENT_NAME);
    }
    
    public TextEditElement getModel() {
        return new TextEditElement(this.driverExt, "model", PARENT_NAME);
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled", PARENT_NAME);
    }     
}
