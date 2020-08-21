package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;

public class LoadGroupVersacomEditPage extends LoadGroupVersacomCreatePage {

    public LoadGroupVersacomEditPage(DriverExtensions driverExt) {
        super(driverExt);
        requiresLogin = true;
    }

}
