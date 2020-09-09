package com.eaton.pages.demandresponse.loadgroup;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;

public class LoadGroupExpresscomEditPage extends LoadGroupExpresscomPage {

    public LoadGroupExpresscomEditPage(DriverExtensions driverExt, int id) {
        super(driverExt, Urls.DemandResponse.LOAD_GROUP_CREATE);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT;
    }

    public String getFeederValueString() {
        return this.driverExt.findElement(By.cssSelector("#feederValueString"), Optional.empty()).getAttribute("value");
    }
}