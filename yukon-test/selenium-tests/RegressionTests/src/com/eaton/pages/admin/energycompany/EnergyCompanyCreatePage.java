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
    
    private TextEditElement companyName;
    private TextEditElement email;
    private DropDownElement defaultRoute;
    private TextEditElement userName;
    private TextEditElement password;
    private TextEditElement confirmPassword;
    
    private PickerElement primaryOperatorGroup;
    private Button saveBtn;
    private Button cancelBtn;

    public EnergyCompanyCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Admin.CREATE_ENERGY_COMPANY;
        
        companyName = new TextEditElement(this.driverExt, "name");
        email = new TextEditElement(this.driverExt, "email");
        defaultRoute = new DropDownElement(this.driverExt, "defaultRouteId");
        userName = new TextEditElement(this.driverExt, "adminUsername");
        password = new TextEditElement(this.driverExt, "adminPassword1");
        confirmPassword = new TextEditElement(this.driverExt, "adminPassword2");
        primaryOperatorGroup = new PickerElement(this.driverExt, "picker-primaryOperatorUserGroupPicker-btn");
        saveBtn = new Button(this.driverExt, "Save");
        cancelBtn = new Button(this.driverExt, "Cancel");
    }
    
    public TextEditElement getCompanyName() {
        return companyName;
    }
    
    public TextEditElement getEmail() {
        return email;
    }
    
    public DropDownElement getDefaultRoute() {
        return defaultRoute;
    }
    
    public TextEditElement getUserName() {
        return userName;
    }
    
    public TextEditElement getPassword() {
        return password;
    }
    
    public TextEditElement getConfirmPassword() {
        return confirmPassword;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }   
    
    private PickerElement getPrimaryOperatorGroup() {
        return primaryOperatorGroup;
    }
    
    public SelectUserGroupModal showAndWaitUserGroupModal() {

        getPrimaryOperatorGroup().clickLink();

        SeleniumTestSetup.waitUntilModalVisibleByTitle("Select User Group");

        return new SelectUserGroupModal(this.driverExt, Optional.empty(), Optional.of("primaryOperatorUserGroupPicker"));
    }
}