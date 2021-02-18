package com.eaton.pages.demandresponse.loadgroup;

import com.eaton.framework.DriverExtensions;

import com.eaton.framework.Urls;


public class LoadGroupDigiSepEditPage extends LoadGroupDigiSepCreatePage{

    public LoadGroupDigiSepEditPage(DriverExtensions driverExt,int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }
}
