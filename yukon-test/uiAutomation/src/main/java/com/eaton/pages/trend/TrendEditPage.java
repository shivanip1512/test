package com.eaton.pages.trend;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class TrendEditPage extends PageBase {

    public TrendEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Tools.TRENDS + id + Urls.EDIT;
    }
    
    public TrendEditPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    // General
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }  
    
    public Button getAddButton() {
        return new Button(this.driverExt, "Add");
    }
    
    public TabElement getTabElement() {
        return new TabElement(this.driverExt);
    }
    
    public Button getSave() {
        return new Button(this.driverExt, "Save");
    }
    
    public Button getCancel() {
        return new Button(this.driverExt, "Cancel");
    }
}
