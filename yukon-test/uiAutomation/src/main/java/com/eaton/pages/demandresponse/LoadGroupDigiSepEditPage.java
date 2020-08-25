package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;

public class LoadGroupDigiSepEditPage extends LoadGroupDigiSepCreatePage{

    public LoadGroupDigiSepEditPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
    }
}
