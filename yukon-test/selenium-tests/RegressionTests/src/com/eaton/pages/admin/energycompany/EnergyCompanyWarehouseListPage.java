package com.eaton.pages.admin.energycompany;

import com.eaton.elements.panels.WarehousePanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class EnergyCompanyWarehouseListPage extends PageBase {
    
    public EnergyCompanyWarehouseListPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);
        
    }
   
    public WarehousePanel getWarehousePanel() {
        return new WarehousePanel(this.driverExt, "Warehouse");
    }
}
