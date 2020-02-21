package com.eaton.pages.capcontrol;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class CbcCreatePage extends PageBase {

    private TextEditElement name;
    private DropDownElement type;
    private TrueFalseCheckboxElement status;
    private TextEditElement serialNumber;
    private DropDownElement controlRoute;

    public CbcCreatePage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        name = new TextEditElement(this.driverExt, "name");
        type = new DropDownElement(this.driverExt, "paoType");
        status = new TrueFalseCheckboxElement(this.driverExt, "disableFlag");
        serialNumber = new TextEditElement(this.driverExt, "deviceCBC.serialNumber");
        controlRoute = new DropDownElement(this.driverExt, "deviceCBC.routeID");
    }

    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getType() {
        return type;
    }

    public TrueFalseCheckboxElement getStatus() {
        return status;
    }

    public TextEditElement getSerialNumber() {
        return serialNumber;
    }

    public DropDownElement getConrolRoute() {
        return controlRoute;
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
