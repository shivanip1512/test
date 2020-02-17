package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class DemandResponseDashboardPage extends PageBase {

    public static final String DEFAULT_URL = Urls.DemandResponse.DASHBOARD;
    public ActionBtnDropDownElement actionBtn;

    public DemandResponseDashboardPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        actionBtn = new ActionBtnDropDownElement(this.driver);
    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    public String getQuickSearchesUrl(String linkText) {
        return this.driver.findElement(By.linkText(linkText)).getAttribute("href");
    }
}
