package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyEditPage extends PageBase {
    
    private TextEditElement companyName;
    private TextEditElement address1;
    private TextEditElement address2;
    private TextEditElement city;
    private TextEditElement state;
    private TextEditElement zip;
    private TextEditElement county;
    private TextEditElement phoneNumber;
    private TextEditElement faxNumber;    
    private TextEditElement email;
    private DropDownElement defaultRoute;
    
    private Button save;
    private Button delete;
    private Button cancel;

    public EnergyCompanyEditPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_EDIT;

        companyName = new TextEditElement(this.driverExt, "name");
        email = new TextEditElement(this.driverExt, "email");
        defaultRoute = new DropDownElement(this.driverExt, "defaultRouteId");

        save = new Button(this.driverExt, "Save");
        delete = new Button(this.driverExt, "Delete");
        cancel = new Button(this.driverExt, "Cancel");
    }
    
    public TextEditElement getCompanyName() {
        return companyName;
    }
    
    public TextEditElement getAddress1() {
        return address1;
    }
    
    public TextEditElement getAddress2() {
        return address2;
    }
    
    public TextEditElement getCity() {
        return city;
    }
    
    public TextEditElement getState() {
        return state;
    }
    
    public TextEditElement getZip() {;
        return zip;
    }
    
    public TextEditElement getCounty() {
        return county;
    }
    
    public TextEditElement getPhoneNumber() {
        return phoneNumber;
    }
    
    public TextEditElement getFaxNumber() {
        return faxNumber;
    }
    
    public TextEditElement getEmail() {
        return email;
    }
    
    public DropDownElement getDefaultRoute() {
        return defaultRoute;
    }        
   
    public Button getSaveBtn() {
        return save;
    }
    
    public Button getDeleteBtn() {
        return delete;
    }
    
    public Button getCancelBtn() {
        return cancel;
    }       
}