package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.pages.PageBase;

public class SubstationBusCreatePage extends PageBase {
    
    private TextEditElement name;
    private Button saveBtn;
    private Button cancelBtn;

    public SubstationBusCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(driver, "name");
        saveBtn = new Button(driver, "Save");
        cancelBtn = new Button(driver, "Cancel");
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
