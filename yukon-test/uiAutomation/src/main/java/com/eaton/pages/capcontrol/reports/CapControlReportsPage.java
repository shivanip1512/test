package com.eaton.pages.capcontrol.reports;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CapControlReportsPage extends PageBase {

    public CapControlReportsPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.REPORTS;
    }    
    
}
