package com.eaton.pages.capcontrol;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CbcEditPage extends PageBase {

    private TextEditElement name;
    private TrueFalseCheckboxElement status;
    private TextEditElement serialNumber;
    private DropDownElement controlRoute;
    private TextEditElement masterAddress;
    private TextEditElement slaveAddress;
    private DropDownElement commChannel;
    private TextEditElement postCommWait;
    private TrueFalseCheckboxElement class0123Scan;
    private TrueFalseCheckboxElement class123Scan;

    public CbcEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.CBC_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
        status = new TrueFalseCheckboxElement(this.driverExt, "disableFlag");
        serialNumber = new TextEditElement(this.driverExt, "deviceCBC.serialNumber");
        controlRoute = new DropDownElement(this.driverExt, "deviceCBC.routeID");
        masterAddress = new TextEditElement(this.driverExt, "deviceAddress.masterAddress");
        slaveAddress = new TextEditElement(this.driverExt, "deviceAddress.slaveAddress");
        commChannel = new DropDownElement(this.driverExt, "deviceDirectCommSettings.portID");
        postCommWait = new TextEditElement(this.driverExt, "deviceAddress.postCommWait");
        class0123Scan = new TrueFalseCheckboxElement(this.driverExt, "editingIntegrity");
        class123Scan = new TrueFalseCheckboxElement(this.driverExt, "editingException");
    }

    public TextEditElement getName() {
        return name;
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
        return masterAddress;
    }

    public TextEditElement getSlaveAddress() {
        return slaveAddress;
    }

    public DropDownElement getCommChannel() {
        return commChannel;
    }

    public TextEditElement getPostCommWait() {
        return postCommWait;
    }

    public TrueFalseCheckboxElement getClass0123Scan() {
        return class0123Scan;
    }

    public TrueFalseCheckboxElement getClass123Scan() {
        return class123Scan;
    }

    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }
    
    public Button getDeleteBtn() {
        return new Button(this.driverExt, "Delete");
    }
    
    public ConfirmModal showAndWaitConfirmDeleteModal() {
        SeleniumTestSetup.moveToElement(getDeleteBtn().getButton());
        
        getDeleteBtn().click();       
                      
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));  
    }
}
