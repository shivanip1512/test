package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;

public class LoadGroupRippleEditPage extends LoadGroupRippleCreatePage {

    public LoadGroupRippleEditPage(DriverExtensions driverExt) {
        super(driverExt);
        requiresLogin = true;
    }

}