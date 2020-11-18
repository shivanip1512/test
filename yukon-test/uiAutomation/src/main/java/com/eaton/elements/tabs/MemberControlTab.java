package com.eaton.elements.tabs;

import com.eaton.elements.SelectBoxElement;
import com.eaton.framework.DriverExtensions;

public class MemberControlTab extends TabElement{

    private DriverExtensions driverExt;
    
    public MemberControlTab(DriverExtensions driverExt) {
        super(driverExt);
        
        this.driverExt = driverExt;
    }

    public SelectBoxElement getMemberControl() {
        return new SelectBoxElement(this.driverExt, this.getTabPanelByName("Member Control"));
    }
}
