package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.pages.PageBase;

public class AreaEditPage extends PageBase {
    
    private TextEditElement name;
    private Button saveBtn;
    private Button cancelBtn;
    private Button deleteBtn;

    public AreaEditPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        saveBtn = new Button(this.driver, "Save");
        cancelBtn = new Button(this.driver, "Cancel");
        deleteBtn = new Button(this.driver, "Delete");
    }

    public String getPageTitle() {
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
    
    public Button getDeleteBtn() {
        return deleteBtn;
    }
}
