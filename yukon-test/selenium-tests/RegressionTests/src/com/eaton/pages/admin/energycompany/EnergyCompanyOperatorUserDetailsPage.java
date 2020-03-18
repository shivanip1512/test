package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class EnergyCompanyOperatorUserDetailsPage extends PageBase {
    
    private Button edit;
    private Button cancel;

    public EnergyCompanyOperatorUserDetailsPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);
        
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
