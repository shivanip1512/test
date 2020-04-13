package com.eaton.pages.admin;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UserEditPage extends PageBase {

    private TextEditElement userName;
    private DropDownElement authentication;
    private DropDownElement userGroup;
    private DropDownElement energyCompany;
    private DropDownElement status;
    private Button save;
    private Button delete;
    private Button cancel;

    public UserEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        this.requiresLogin = true;
        pageUrl = Urls.Admin.USER_EDIT + id + Urls.EDIT;
        
        userName = new TextEditElement(this.driverExt, "username");
        authentication = new DropDownElement(this.driverExt, "authCategory");
        userGroup = new DropDownElement(this.driverExt, "userGroupId");
        energyCompany = new DropDownElement(this.driverExt, "energyCompanyId");
        status = new DropDownElement(this.driverExt, "loginStatus");
        save = new Button(this.driverExt, "Save");
        delete = new Button(this.driverExt, "Delete");
        cancel = new Button(this.driverExt, "Cancel");
    }
    
    public TextEditElement getUserName() {
        return userName;
    }
    
    public DropDownElement getAuthentication() {
        return authentication;
    }

    public DropDownElement getUserGroup() {
        return userGroup;
    }
    
    public DropDownElement getEnergyCompany() {
        return energyCompany;
    }
    
    public DropDownElement getStatus() {
        return status;
    }
    
    public Button getSave() {
        return save;
    }
    
    public Button getCance() {
        return cancel;
    }
    
    public Button getDelete() {
        return delete;
    }
}