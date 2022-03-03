package com.eaton.elements.panels;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.NameValueTable;
import com.eaton.elements.WebTable;
import com.eaton.framework.DriverExtensions;

public class OutagesPanel extends BasePanel {

    private DriverExtensions driverExt;
    
    public OutagesPanel(DriverExtensions driverExt, String panelName) {
        super(driverExt, panelName);
        
        this.driverExt = driverExt;
    }

    public NameValueTable getOutageTable() {
        return new NameValueTable(this.driverExt, getPanel(), Optional.empty());
    }
    
    public WebTable getRfnOutageLogTable() {
        return new WebTable(this.driverExt, "compact-results-table");
    }
    
    public String getRfnOutageLogTitle() {
        return getPanel().findElement(By.cssSelector("#outageLog .title")).getText();
    }
}
