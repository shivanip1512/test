package com.eaton.pages.admin.energycompany;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.SelectUserModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyOperatorUserListPage extends PageBase {
    
    private Button create;
    private Button add;

    public EnergyCompanyOperatorUserListPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_LIST + id;
        
        create = new Button(this.driverExt, "Create");
        add = new Button(this.driverExt, "Add");
    }
    
    public Button getCreateBtn() {
        return create;
    }
    
    private Button getAddBtn() {
        return add;
    }

    public SelectUserModal showAndWaitSelectUserModal() {
        
        getAddBtn().click();      
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("ecOperatorCandidatePicker");
        
        return new SelectUserModal(this.driverExt, Optional.empty(), Optional.of("ecOperatorCandidatePicker"));
    }  
    
    public WebTable getTable() {
        return new WebTable(this.driverExt, "compact-results-table");
    }
}
