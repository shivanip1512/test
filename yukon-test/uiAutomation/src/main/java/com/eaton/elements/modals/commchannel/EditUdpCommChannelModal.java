package com.eaton.elements.modals.commchannel;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.Section;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class EditUdpCommChannelModal extends EditCommChannelModal {

    public EditUdpCommChannelModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
    }    

    public Section getGeneralSection() {
        return new Section(this.driverExt, "General", getConfigTab());
    }
    
    
    public Section getSharedSection() {
        return new Section(this.driverExt, "Shared", getConfigTab());
    }
    
    //Info Tab    
    public TextEditElement getPortNumber() {
        return new TextEditElement(this.driverExt, "portNumber", getInfoTab());
    }
    
    public DropDownElement getBaudRate() {
        return new DropDownElement(this.driverExt, "baudRate", getInfoTab());
    }
    
    //Configuration Tab - General Section    
    public RadioButtonElement getProtocolWrap() {
        return new RadioButtonElement(this.driverExt, "protocolWrap", getGeneralSection().getSection());
    }
    
    public SwitchBtnYesNoElement getCarrierDetectWait() {
        return new SwitchBtnYesNoElement(this.driverExt, "carrierDetectWait", getGeneralSection().getSection());
    }

    public TextEditElement getCarrierDetectWaitTextBox() {
        return new TextEditElement(this.driverExt, "carrierDetectWaitInMilliseconds", getGeneralSection().getSection());
    }
    
    public SwitchBtnYesNoElement getEncryptionKey() {
        return new SwitchBtnYesNoElement(this.driverExt, "encryptionKey", getGeneralSection().getSection());
    }

    public TextEditElement getEncryptionKeyTextBox() {
        return new TextEditElement(this.driverExt, "keyInHex", getGeneralSection().getSection());
    }       

    //Configuration Tab - Shared Section    
    public RadioButtonElement getSharedPortType() {
        return new RadioButtonElement(this.driverExt, "sharing.sharedPortType", getSharedSection().getSection());
    }
    
    public TextEditElement getSocketNumber() {
        return new TextEditElement(this.driverExt, "sharing.sharedSocketNumber", getSharedSection().getSection());
    }        
}
