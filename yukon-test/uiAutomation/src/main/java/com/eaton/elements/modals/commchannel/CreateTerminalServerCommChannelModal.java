package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CreateTerminalServerCommChannelModal extends CreateCommChannelModal {
    
    private TextEditElement ipAddress;
    private TextEditElement portNumber;

    public CreateTerminalServerCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle,
            Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        ipAddress = new TextEditElement(this.driverExt, "ipAddress", ARIADESCRIBEDBY);
        portNumber = new TextEditElement(this.driverExt, "portNumber", ARIADESCRIBEDBY);
    }
    
    public TextEditElement getIpAddress() {
        return ipAddress;
    }
    
    public TextEditElement getPortNumber() {
        return portNumber;
    } 
}
