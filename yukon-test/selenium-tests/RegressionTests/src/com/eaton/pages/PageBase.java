package com.eaton.pages;

import org.openqa.selenium.WebDriver;

public class PageBase implements ISeleniumPage {

    protected String pageUrl;
    protected Boolean requiresLogin;
    protected WebDriver driver;

    public PageBase(WebDriver driver, String pageUrl) {

        driver = driver;
        pageUrl = pageUrl;
    }

    @Override
    public String getPageUrl() {

        return pageUrl;
    }

    @Override
    public boolean getRequiresLogin() {

        return requiresLogin;
    }
}
