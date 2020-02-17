package com.eaton.pages.ami;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.EditMeterModal;
import com.eaton.elements.panels.MeterInfoPanel;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class MeterDetailsPage extends PageBase {

    public MeterDetailsPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

    }

    public String getPageTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    public String getUserMessage() {
        return this.driver.findElement(By.cssSelector(".yukon-content .user-message")).getText();
    }
    
    public ActionBtnDropDownElement getAction() {
        return new ActionBtnDropDownElement(this.driver);
    }

    public MeterInfoPanel getMeterInfoPanel() {
        return new MeterInfoPanel(this.driver, "Meter Info");
    }

    public EditMeterModal showMeterEditModal() {

        getMeterInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalVisible("meter-info-popup");

        return new EditMeterModal(this.driver, "meter-info-popup");
    }
}
