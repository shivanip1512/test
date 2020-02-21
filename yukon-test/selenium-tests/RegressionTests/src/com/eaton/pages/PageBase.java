package com.eaton.pages;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;

public class PageBase implements ISeleniumPage {

    protected String pageUrl;
    protected Boolean requiresLogin;
    protected DriverExtensions driverExt;

    public PageBase(DriverExtensions driverExt, String pageUrl) {

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
    
    public String getPageTitle() {
        return this.driverExt.findElement(By.cssSelector(".page-heading"), Optional.empty()).getText();
    }
    
    public String getUserMessage() {
        return this.driverExt.findElement(By.cssSelector(".yukon-content .user-message"), Optional.empty()).getText();
    }
}
