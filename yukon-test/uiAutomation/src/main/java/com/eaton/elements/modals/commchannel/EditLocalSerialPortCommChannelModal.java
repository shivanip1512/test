package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.framework.DriverExtensions;

public class EditLocalSerialPortCommChannelModal extends EditCommChannelModal {

    public EditLocalSerialPortCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    public DropDownElement getPhysicalPort() {
        return new DropDownElement(this.driverExt, "physicalPort");
    }
    
    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "baudRate");
    }
}
