package com.eaton.pages;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.framework.DriverExtensions;

public class PageBase implements ISeleniumPage {

    protected Boolean requiresLogin;
    protected String pageUrl;
    protected DriverExtensions driverExt;

    public PageBase(DriverExtensions driverExt) {

        this.driverExt = driverExt;
    }
    
    @Override
    public String getPageUrl() {
        return pageUrl;
    }

    @Override
    public boolean getRequiresLogin() {

        return this.requiresLogin;
    }
    
    public String getPageTitle() {
        return this.driverExt.findElement(By.cssSelector(".page-heading"), Optional.empty()).getText();
    }
    
    public String getUserMessage() {
        return this.driverExt.findElement(By.cssSelector(".yukon-content .user-message"), Optional.of(2)).getText();
    }
    
    public String getLinkedPageTitle() {
        return this.driverExt.findElement(By.cssSelector(".category .title"), Optional.empty()).getText();
    }
}
