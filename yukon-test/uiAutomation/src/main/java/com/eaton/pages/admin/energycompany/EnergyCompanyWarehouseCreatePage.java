package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.elements.MultiLineTextElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyWarehouseCreatePage extends PageBase {
    
    private TextEditElement warehouseName;
    private TextEditElement address;
    private TextEditElement address2;
    private TextEditElement city;
    private TextEditElement state;
    private TextEditElement zip;
    private MultiLineTextElement notes;
    
    private Button save;
    private Button cancel;

    public EnergyCompanyWarehouseCreatePage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_WAREHOUSE_CREATE + id;
        
        warehouseName = new TextEditElement(this.driverExt, "warehouse.warehouseName");
        address = new TextEditElement(this.driverExt, "address.locationAddress1");
        address2 = new TextEditElement(this.driverExt, "address.locationAddress2");
        city = new TextEditElement(this.driverExt, "address.cityName");
        state = new TextEditElement(this.driverExt, "address.stateCode");
        zip = new TextEditElement(this.driverExt, "address.zipCode");
        notes = new MultiLineTextElement(this.driverExt, "warehouse.notes");
        
        save = new Button(this.driverExt, "Save");
        cancel = new Button(this.driverExt, "Cancel");
    }

    public TextEditElement getWarehouseName() {
        return warehouseName;
    }
    
    public TextEditElement getAddress() {
        return address;
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
    
    public MultiLineTextElement getNotes() {
        return notes;
    }
    
    public Button getSaveBtn() {
        return save;
    }
    
    public Button getCancelBtn() {
        return cancel;
    }
}
