package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.pages.PageBase;

public class CbcCreatePage extends PageBase {

    private TextEditElement name;
    private DropDownElement type;
    private TrueFalseCheckboxElement status;
    private TextEditElement serialNumber;
    private DropDownElement controlRoute;

    public CbcCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        type = new DropDownElement(this.driver, "paoType");
        status = new TrueFalseCheckboxElement(this.driver, "disableFlag");
        serialNumber = new TextEditElement(this.driver, "deviceCBC.serialNumber");
        controlRoute = new DropDownElement(this.driver, "deviceCBC.routeID");
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
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
        return new TextEditElement(this.driver, "deviceAddress.masterAddress");
    }

    public TextEditElement getSlaveAddress() {
        return new TextEditElement(this.driver, "deviceAddress.slaveAddress");
    }

    public DropDownElement getCommChannel() {
        return new DropDownElement(this.driver, "deviceDirectCommSettings.portID");
    }

    public TextEditElement getPostCommWait() {
        return new TextEditElement(this.driver, "deviceAddress.postCommWait");
    }

    public TrueFalseCheckboxElement getClass0123Scan() {
        return new TrueFalseCheckboxElement(this.driver, "editingIntegrity");
    }

    public TrueFalseCheckboxElement getClass123Scan() {
        return new TrueFalseCheckboxElement(this.driver, "editingException");
    }

    public Button getSaveBtn() {
        return new Button(this.driver, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driver, "Cancel");
    }
}
