package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyListPage  extends PageBase {
    
    private Button createBtn;

    public EnergyCompanyListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_LIST;

        createBtn = new Button(driverExt, "Create");
    }
    
    public Button getCreateBtn() {
        return createBtn;
    }   
}
