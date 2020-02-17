package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.pages.PageBase;

public class FeederCreatePage extends PageBase {
    
    private TextEditElement name;
    private TrueFalseCheckboxElement status;
    private TextEditElement mapLocationId;
    private Button saveBtn;
    private Button cancelBtn;

    public FeederCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        status = new TrueFalseCheckboxElement(this.driver, "disabled");
        mapLocationId = new TextEditElement(this.driver, "capControlFeeder.mapLocationID");
        
        saveBtn = new Button(this.driver, "Save");
        cancelBtn = new Button(this.driver, "Cancel");
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public TextEditElement getMapLocationId() {
        return mapLocationId;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
