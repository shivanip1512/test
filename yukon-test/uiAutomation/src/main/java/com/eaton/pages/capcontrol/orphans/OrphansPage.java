package com.eaton.pages.capcontrol.orphans;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class OrphansPage extends PageBase {
    

    public OrphansPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.CapControl.ORPHANS;
    }
}