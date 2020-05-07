package com.eaton.elements.modals.gears;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class CreateGearsModal extends BaseModal {

    DriverExtensions driverExt;
    private TextEditElement gearName;
    private DropDownElement gearType;
    private WebElement modal;

    public CreateGearsModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);

        this.driverExt = driverExt;
        modal = getModal();
        gearName = new TextEditElement(this.driverExt, "gearName", modal);
        gearType = new DropDownElement(this.driverExt, "controlMethod", modal);
    }

    public TextEditElement getGearName() {
        return gearName;
    }

    public DropDownElement getGearType() {
        return gearType;
    }
}
