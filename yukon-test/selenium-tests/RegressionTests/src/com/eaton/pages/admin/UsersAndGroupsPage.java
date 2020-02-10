package com.eaton.pages.admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class UsersAndGroupsPage extends PageBase {

    public static final String DEFAULT_URL = Urls.Admin.USERS_AND_GROUPS;

    public UsersAndGroupsPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

}
