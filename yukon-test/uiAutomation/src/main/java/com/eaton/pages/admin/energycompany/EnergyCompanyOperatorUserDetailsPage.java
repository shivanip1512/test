package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyOperatorUserDetailsPage extends PageBase {
    
    private Button edit;
    private Button cancel;

    public EnergyCompanyOperatorUserDetailsPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_LIST + id;
        
        edit = new Button(this.driverExt, "Edit");
        cancel = new Button(this.driverExt, "Cancel");
    }
    
    public Button getEditBtn() {
        return edit;
    }
    
    public Button getCancelBtn() {
        return cancel;
    }
}
