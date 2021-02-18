package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class EditTerminalServerCommChannelModal extends EditCommChannelModal {

    public EditTerminalServerCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General", getConfigTab());
    }
    
    public Section getSharedSection() {
        return new Section(this.driverExt, "Shared", getConfigTab());
    }
    
    //Info
    public TextEditElement getIpAddress() {
        return new TextEditElement(this.driverExt, "ipAddress", getModal());
    }
    
    public TextEditElement getPortNumber() {
        return new TextEditElement(this.driverExt, "portNumber", getModal());
    }
    
    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "baudRate");
    }
    
    //Configuration - General Section    
    public RadioButtonElement getProtocolWrap() {
        return new RadioButtonElement(this.driverExt, "protocolWrap", getModal());
    }
    
    public SwitchBtnYesNoElement getCarrierDetectWait() {
        return new SwitchBtnYesNoElement(this.driverExt, "carrierDetectWait", getModal());
    }
    
    public TextEditElement getCarrierDetectWaitTextBox() {
        return new TextEditElement(this.driverExt, "carrierDetectWaitInMilliseconds", getModal());
    }
    
    
    //Configuration - Shared Section
    public RadioButtonElement getSharedPortType() {
        return new RadioButtonElement(this.driverExt, "sharing.sharedPortType");
    }
    
    public TextEditElement getSocketNumber() {
       return new TextEditElement(this.driverExt, "sharing.sharedSocketNumber", getModal());
    }  
}
