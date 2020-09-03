package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupMCTEditPage extends LoadGroupMCTCreatePage {

    public LoadGroupMCTEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }

}
