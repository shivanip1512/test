package com.eaton.elements.modals.gears;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;

public class CreateGearsModal extends BaseModal {

    WebDriver driver;
    private TextEditElement gearName;
    private DropDownElement gearType;

    public CreateGearsModal(WebDriver driver, String modalName) {
        super(driver, modalName);

        gearName = new TextEditElement(this.driver, "gearName", null);
        gearType = new DropDownElement(this.driver, "controlMethod", null);
    }

    public TextEditElement getGearName() {
        return gearName;
    }

    public DropDownElement getGearType() {
        return gearType;
    }

    // TODO work around until get a unique way to select element
    public void clickSave() {
        this.driver.findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();
    }

    // TODO work around until get a unique way to select element
    public void clickCancel() {
        this.driver.findElement(By.cssSelector(".ui-dialog-buttonset .js-secondary-action")).click();
    }
}
