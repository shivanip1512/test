package com.eaton.elements.panels;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class WarehousePanel extends BasePanel {

    private DriverExtensions driverExt;
    private WebElement panel;
    
    public WarehousePanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
        this.panel = super.getPanel();
    }

    public Button getCreateBtn() {
        return new Button(this.driverExt, "createWarehouse", this.panel);
    }  
    
    public WebTable getTable() {
        return new WebTable(this.driverExt, "compact-results-table", this.panel);
    }
}
