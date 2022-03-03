package com.eaton.elements.tabs;

import com.eaton.elements.SelectBoxElement;
import com.eaton.framework.DriverExtensions;

public class NotificationTab extends TabElement{

    private DriverExtensions driverExt;
    
    public NotificationTab(DriverExtensions driverExt) {
        super(driverExt);
        
        this.driverExt = driverExt;
    }

    public SelectBoxElement getNotification() {
        return new SelectBoxElement(this.driverExt, this.getTabPanelByName("Notification"));
    }
}
