package com.eaton.pages;

import org.openqa.selenium.WebDriver;

public class PageBase implements ISeleniumPage {

    protected String pageUrl;
    protected Boolean requiresLogin;
    protected WebDriver driver;

    public PageBase(WebDriver driver, String pageUrl) {

        this.driver = driver;
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
