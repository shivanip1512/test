package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CreateLocalSerialPortCommChannelModal extends CreateCommChannelModal {
    
    private DropDownElement physicalPort;
    private TextEditElement physicalPortOther;

    public CreateLocalSerialPortCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle,
            Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        physicalPort = new DropDownElement(this.driverExt, "physicalPort", ARIADESCRIBEDBY);
        physicalPortOther = new TextEditElement(this.driverExt, "physicalPort", ARIADESCRIBEDBY);
    }
    
    public DropDownElement getPhysicalPort() {
        return physicalPort;
    }
    
    public TextEditElement getPhysicalPortOther() {
        return physicalPortOther;
    }
}
