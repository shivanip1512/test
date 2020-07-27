package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupHoneywellCreateTest extends LoadGroupCreatePage {
	
	public LoadGroupHoneywellCreateTest(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_CREATE;
    }
}