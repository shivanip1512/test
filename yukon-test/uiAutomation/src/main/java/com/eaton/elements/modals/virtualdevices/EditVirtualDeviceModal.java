package com.eaton.elements.modals.virtualdevices;

import java.util.Optional;

import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class EditVirtualDeviceModal extends BaseModal {
    
    private TextEditElement name;
    private SwitchBtnYesNoElement switchBtnYesNoElement;

    //protected static final String ARIA_DESCRIBED_BY = "js-edit-virtual-device-popup";
    
    public EditVirtualDeviceModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        this.name = new TextEditElement(this.driverExt, "name",getModal() );
        this.switchBtnYesNoElement = new SwitchBtnYesNoElement(this.driverExt, "enable", getModal());
    }

    public TextEditElement getName() {
        return name;
    }        
    
    public SwitchBtnYesNoElement getStatus() {
        return switchBtnYesNoElement;
    }
}
