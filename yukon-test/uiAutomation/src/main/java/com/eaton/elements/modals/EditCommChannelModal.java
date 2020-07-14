package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;

public class EditCommChannelModal extends BaseModal {

    public EditCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }

    // TODO need to add all fields as there are more based on the device you select

    public TextEditElement getChannelName() {
        return new TextEditElement(this.driverExt, "name", getModal());
    }

    public TextEditElement getIPAddress() {
        return new TextEditElement(this.driverExt, "meterNumber", getModal());
    }

    public TextEditElement getPortNumber() {
        return new TextEditElement(this.driverExt, "address", getModal());
    }

    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "routeId", getModal());
    }

    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disabled", getModal());
    }
}
