package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CbcCreatePage extends PageBase {

    public CbcCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.CBC_CREATE;
    }

    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }

    public DropDownElement getType() {
        return new DropDownElement(this.driverExt, "paoType");
    }

    public TrueFalseCheckboxElement getStatus() {
        return new TrueFalseCheckboxElement(this.driverExt, "disableFlag");
    }

    public TextEditElement getSerialNumber() {
        return new TextEditElement(this.driverExt, "deviceCBC.serialNumber");
    }

    public DropDownElement getConrolRoute() {
        return new DropDownElement(this.driverExt, "deviceCBC.routeID");
    }

    public TextEditElement getMasterAddress() {
        return new TextEditElement(this.driverExt, "deviceAddress.masterAddress");
    }

    public TextEditElement getSlaveAddress() {
        return new TextEditElement(this.driverExt, "deviceAddress.slaveAddress");
    }

    public DropDownElement getCommChannel() {
        return new DropDownElement(this.driverExt, "deviceDirectCommSettings.portID");
    }
    
    public TextEditElement getIpAddress() {
        return new TextEditElement(this.driverExt, "ipAddress");                
    }
    
    public TextEditElement getPort() {
        return new TextEditElement(this.driverExt, "port");
    }

    public TextEditElement getPostCommWait() {
        return new TextEditElement(this.driverExt, "deviceAddress.postCommWait");
    }

    public TrueFalseCheckboxElement getClass0123Scan() {
        return new TrueFalseCheckboxElement(this.driverExt, "editingIntegrity");
    }

    public TrueFalseCheckboxElement getClass123Scan() {
        return new TrueFalseCheckboxElement(this.driverExt, "editingException");
    }

    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }
}
