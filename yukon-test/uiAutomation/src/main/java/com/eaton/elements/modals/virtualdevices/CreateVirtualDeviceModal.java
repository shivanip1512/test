package com.eaton.elements.modals.virtualdevices;

import java.util.Optional;

import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class CreateVirtualDeviceModal extends BaseModal {
    
    private TextEditElement name;

    protected static final String ARIADESCRIBEDBY = "js-create-virtual-device-popup";
    
    public CreateVirtualDeviceModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        name = new TextEditElement(this.driverExt, "name", ARIADESCRIBEDBY);
    }

    public TextEditElement getName() {
    	return name;
    }        
    
    public SwitchBtnYesNoElement getStatus() {
        return new SwitchBtnYesNoElement(this.driverExt, "enable", getModal());
    }
}
