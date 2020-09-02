package com.eaton.pages.demandresponse;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupItronEditPage extends LoadGroupCreatePage{

	public LoadGroupItronEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }

}

