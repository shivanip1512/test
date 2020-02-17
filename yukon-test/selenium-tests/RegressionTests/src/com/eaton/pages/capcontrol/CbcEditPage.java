package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
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

    public CbcEditPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        status = new TrueFalseCheckboxElement(this.driver, "disableFlag");
        serialNumber = new TextEditElement(this.driver, "deviceCBC.serialNumber");
        controlRoute = new DropDownElement(this.driver, "deviceCBC.routeID");
        masterAddress = new TextEditElement(this.driver, "deviceAddress.masterAddress");
        slaveAddress = new TextEditElement(this.driver, "deviceAddress.slaveAddress");
        commChannel = new DropDownElement(this.driver, "deviceDirectCommSettings.portID");
        postCommWait = new TextEditElement(this.driver, "deviceAddress.postCommWait");
        class0123Scan = new TrueFalseCheckboxElement(this.driver, "editingIntegrity");
        class123Scan = new TrueFalseCheckboxElement(this.driver, "editingException");
    }

    public String getPageTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
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
        return new Button(this.driver, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driver, "Cancel");
    }
    
    public Button getDeleteBtn() {
        return new Button(this.driver, "Delete");
    }
}
