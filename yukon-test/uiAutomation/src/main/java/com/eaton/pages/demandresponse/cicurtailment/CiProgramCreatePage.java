package com.eaton.pages.demandresponse.cicurtailment;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CiProgramCreatePage extends PageBase {

    public CiProgramCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CI_PROGRAM_CREATE;
    }
}
