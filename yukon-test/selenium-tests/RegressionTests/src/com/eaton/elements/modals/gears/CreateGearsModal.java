package com.eaton.elements.modals.gears;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.framework.DriverExtensions;

public class CreateGearsModal extends BaseModal {

    DriverExtensions driverExt;
    private TextEditElement gearName;
    private DropDownElement gearType;

    public CreateGearsModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);

        this.driverExt = driverExt;
        gearName = new TextEditElement(this.driverExt, "gearName");
        gearType = new DropDownElement(this.driverExt, "controlMethod");
    }

    public TextEditElement getGearName() {
        return gearName;
    }

    public DropDownElement getGearType() {
        return gearType;
    }
}
