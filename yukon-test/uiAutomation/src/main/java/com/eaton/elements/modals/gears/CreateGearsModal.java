package com.eaton.elements.modals.gears;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class CreateGearsModal extends BaseModal {

    private WebElement modal;

    public CreateGearsModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        modal = getModal();
    }

    public TextEditElement getGearName() {
        return new TextEditElement(this.driverExt, "gearName", modal);
    }

    public DropDownElement getGearType() {
        return new DropDownElement(this.driverExt, "controlMethod", modal);
    }
}
