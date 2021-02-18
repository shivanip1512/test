package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CreateUdpCommChannelModal extends CreateCommChannelModal {

    private TextEditElement portNumber;
    
    public CreateUdpCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        portNumber = new TextEditElement(this.driverExt, "portNumber", ARIADESCRIBEDBY);
    }
    
    public TextEditElement getPortNumber() {
        return portNumber;
    } 

}
