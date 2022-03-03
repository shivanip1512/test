package com.eaton.pages.demandresponse.cicurtailment;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class GroupCreatePage extends PageBase {

    public GroupCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.CI_GROUP_CREATE;
    }
}
