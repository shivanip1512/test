package com.eaton.pages.admin.energycompany;

import com.eaton.elements.Button;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class EnergyCompanyDeleteConfirmationPage extends PageBase {
    
    private Button deleteBtn;
    private Button cancelBtn;

    public EnergyCompanyDeleteConfirmationPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

        deleteBtn = new Button(driverExt, "Delete");
        cancelBtn = new Button(driverExt, "Cancel");
    }

    public Button getDeleteBtn() {
        return deleteBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }
}
