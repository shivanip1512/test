package com.eaton.pages.capcontrol;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CbcDetailPage  extends PageBase {
    

    public CbcDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.CBC_DETAIL + id;
    }
    
    public CbcDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
}