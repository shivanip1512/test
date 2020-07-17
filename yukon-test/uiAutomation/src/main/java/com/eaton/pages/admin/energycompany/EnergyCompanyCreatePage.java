package com.eaton.pages.admin.energycompany;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.SelectUserGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyCreatePage extends PageBase {
    
    public EnergyCompanyCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Admin.CREATE_ENERGY_COMPANY;
    }
    
    public TextEditElement getCompanyName() {
        return new TextEditElement(this.driverExt, "name");
    }
    
    public TextEditElement getEmail() {
        return new TextEditElement(this.driverExt, "email");
    }
    
    public DropDownElement getDefaultRoute() {
        return new DropDownElement(this.driverExt, "defaultRouteId");
    }
    
    public TextEditElement getUserName() {
        return new TextEditElement(this.driverExt, "adminUsername");
    }
    
    public TextEditElement getPassword() {
        return new TextEditElement(this.driverExt, "adminPassword1");
    }
    
    public TextEditElement getConfirmPassword() {
        return new TextEditElement(this.driverExt, "adminPassword2");
    }
    
    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }   
    
    private PickerElement getPrimaryOperatorGroup() {
        return new PickerElement(this.driverExt, "picker-primaryOperatorUserGroupPicker-btn");
    }
    
    public SelectUserGroupModal showAndWaitUserGroupModal() {

        getPrimaryOperatorGroup().clickLink();

        SeleniumTestSetup.waitUntilModalVisibleByTitle("Select User Group");

        return new SelectUserGroupModal(this.driverExt, Optional.empty(), Optional.of("primaryOperatorUserGroupPicker"));
    }
}