package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class EnergyCompanyListPage  extends PageBase {
    
    private Button createBtn;

    public EnergyCompanyListPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        createBtn = new Button(driverExt, "Create");
    }
    
    public Button getCreateBtn() {
        return createBtn;
    }   
}
