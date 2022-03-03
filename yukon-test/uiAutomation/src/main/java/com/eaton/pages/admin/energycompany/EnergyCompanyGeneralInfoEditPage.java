package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyGeneralInfoEditPage extends PageBase {
    
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
    
    private Button saveBtn;
    private Button deleteBtn;
    private Button cancelBtn;

    public EnergyCompanyGeneralInfoEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_EDIT + id;

        companyName = new TextEditElement(this.driverExt, "name");
        address1 = new TextEditElement(this.driverExt, "address.locationAddress1");
        address2 = new TextEditElement(this.driverExt, "address.locationAddress2");
        city = new TextEditElement(this.driverExt, "address.cityName");
        state = new TextEditElement(this.driverExt, "address.stateCode");
        zip = new TextEditElement(this.driverExt, "address.zipCode");
        county = new TextEditElement(this.driverExt, "address.county");
        phoneNumber = new TextEditElement(this.driverExt, "phone");
        faxNumber = new TextEditElement(this.driverExt, "fax");        
        email = new TextEditElement(this.driverExt, "email");
        defaultRoute = new DropDownElement(this.driverExt, "defaultRouteId");
        saveBtn = new Button(this.driverExt, "Save");
        deleteBtn = new Button(this.driverExt, "Delete");
        cancelBtn = new Button(this.driverExt, "Cancel");
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
    
    public TextEditElement getZip() {
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
        return saveBtn;
    }
    
    public Button getDeleteBtn() {
        return deleteBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }       
}