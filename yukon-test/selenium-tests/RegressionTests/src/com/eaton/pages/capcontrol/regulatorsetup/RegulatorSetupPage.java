package com.eaton.pages.capcontrol.regulatorsetup;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class RegulatorSetupPage extends PageBase {

    public RegulatorSetupPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.CapControl.REGULATOR_SETUP;
    }
}
