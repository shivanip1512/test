package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.framework.DriverExtensions;

public class EditTcpCommChannelModal extends EditCommChannelModal {

    public EditTcpCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General", getConfigTab());
    }
    
    public Section getSharedSection() {
        return new Section(this.driverExt, "Shared", getConfigTab());
    }
    
    //Info
    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "baudRate");
    }
    
    //Configuration - General Section
    public RadioButtonElement getProtocolWrap() {
        return new RadioButtonElement(this.driverExt, "protocolWrap", getGeneralSection().getSection());
    }
    
    public SwitchBtnYesNoElement getCarrierDetectWait() {
        return new SwitchBtnYesNoElement(this.driverExt, "carrierDetectWait", getGeneralSection().getSection());
    }
    
    //Configuration - Shared Section
    public RadioButtonElement getSharedPortType() {
        return new RadioButtonElement(this.driverExt, "sharing.sharedPortType", getGeneralSection().getSection());
    }
}
