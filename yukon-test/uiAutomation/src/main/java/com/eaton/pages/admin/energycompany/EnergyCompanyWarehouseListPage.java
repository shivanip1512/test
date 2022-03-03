package com.eaton.pages.admin.energycompany;

import com.eaton.elements.panels.WarehousePanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class EnergyCompanyWarehouseListPage extends PageBase {
    
    public EnergyCompanyWarehouseListPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ENERGY_COMPANY_LIST + id;
    }
    
    public EnergyCompanyWarehouseListPage(DriverExtensions driverExt) {
        super(driverExt);
    }
   
    public WarehousePanel getWarehousePanel() {
        return new WarehousePanel(this.driverExt, "Warehouse");
    }
}
