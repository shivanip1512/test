package com.eaton.pages.ami;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.CreateMeterModal;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class AmiDashboardPage extends PageBase {

    private ActionBtnDropDownElement actionBtn;

    public AmiDashboardPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        actionBtn = new ActionBtnDropDownElement(this.driver);
    }

    public String getTitle() {

        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    } 
    
    public String getUserMessage() {
        return this.driver.findElement(By.cssSelector(".yukon-content .user-message")).getText();
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
    
    public CreateMeterModal showCreateMeterModal() {
        
        actionBtn.clickAndSelectOptionByText("Create Meter");        
                      
        SeleniumTestSetup.waitUntilModalVisible("contentPopup");
        
        return new CreateMeterModal(this.driver, "contentPopup");        
    }    
}
