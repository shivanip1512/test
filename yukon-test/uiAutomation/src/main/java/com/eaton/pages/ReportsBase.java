package com.eaton.pages;

import com.eaton.framework.DriverExtensions;

public class ReportsBase implements ISeleniumPage {

    protected String pageUrl;
    protected Boolean requiresLogin;
    protected DriverExtensions driverExt;

    public ReportsBase(DriverExtensions driverExt, String pageUrl) {

        this.driverExt = driverExt;
        this.pageUrl = pageUrl;
    }

    @Override
    public String getPageUrl() {

        return this.pageUrl;
    }

    @Override
    public boolean getRequiresLogin() {

        return this.requiresLogin;
    }
    
    

}
