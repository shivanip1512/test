package com.eaton.elements.tabs;

import com.eaton.elements.SelectBoxElement;
import com.eaton.framework.DriverExtensions;

public class LoadGroupsTab extends TabElement{

    private DriverExtensions driverExt;
    
    public LoadGroupsTab(DriverExtensions driverExt) {
        super(driverExt);
        
        this.driverExt = driverExt;
    }

    public SelectBoxElement getLoadGroups() {
        return new SelectBoxElement(this.driverExt, this.getTabPanelByName("Load Groups"));
    }
}
