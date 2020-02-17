package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class DemandResponseSetupPage extends PageBase {

    public static final String DEFAULT_URL = Urls.DemandResponse.SETUP;
    private ActionBtnDropDownElement actionBtn;

    public DemandResponseSetupPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        this.requiresLogin = true;
        pageUrl = DEFAULT_URL;
        actionBtn = new ActionBtnDropDownElement(this.driver);
    }

    public String getPageTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    public String getUserMessage() {
        return this.driver.findElement(By.cssSelector(".user-message")).getText();
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
}
