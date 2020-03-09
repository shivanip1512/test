package com.eaton.elements.modals;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CreateUserModal extends BaseModal {

    private DriverExtensions driverExt;
    private TextEditElement userName;
    private DropDownElement authentication;
    private TextEditElement password;
    private TextEditElement confirmPassword;
    private DropDownElement userGroup;
    private DropDownElement energyCompany;
    private static final String PARENT = "ui-id-8";

    // TODO no unique way to get modal
    // TODO enable/disable is a new kind of checkbox, should this be changed to match the others?
    // TODO cancel and save buttons do not have a unique way to select them

    public CreateUserModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);

        this.driverExt = driverExt;

        userName = new TextEditElement(this.driverExt, "username", PARENT);
        authentication = new DropDownElement(this.driverExt, "authCategory", PARENT);
        password = new TextEditElement(this.driverExt, "password.password", PARENT);
        confirmPassword = new TextEditElement(this.driverExt, "password.confirmPassword", PARENT);
        userGroup = new DropDownElement(this.driverExt, "userGroupId", PARENT);
        energyCompany = new DropDownElement(this.driverExt, "energyCompanyId", PARENT);
    }

    public TextEditElement getUserName() {
        return userName;
    }

    public DropDownElement getAuthentication() {
        return authentication;
    }

    public TextEditElement getPassword() {
        return password;
    }

    public TextEditElement getConfirmPassword() {
        return confirmPassword;
    }

    public DropDownElement getUserGroup() {
        return userGroup;
    }

    public DropDownElement getEnergyCompany() {
        return energyCompany;
    }
}
