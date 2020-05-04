package com.eaton.pages.demandresponse.cicurtailment;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CiCurtailmentPage extends PageBase {

    public CiCurtailmentPage(DriverExtensions driverExt, String baseUrl) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CI_CURTAILMENT;
    }
}
