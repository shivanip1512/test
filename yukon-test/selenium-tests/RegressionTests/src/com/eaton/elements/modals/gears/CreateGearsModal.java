package com.eaton.elements.modals.gears;

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

        gearName = new TextEditElement(this.driver, "gearName");
        gearType = new DropDownElement(this.driver, "controlMethod");
    }

    public TextEditElement getGearName() {
        return gearName;
    }

    public DropDownElement getGearType() {
        return gearType;
    }
}
