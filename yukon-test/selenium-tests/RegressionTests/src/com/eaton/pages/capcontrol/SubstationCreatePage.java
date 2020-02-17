package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.pages.PageBase;

public class SubstationCreatePage extends PageBase {
    
    private TextEditElement name;
    private TextEditElement geoName;
    private TextEditElement mapLocation;
    private TrueFalseCheckboxElement status;
    private Button saveBtn;
    private Button cancelBtn;

    public SubstationCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        geoName = new TextEditElement(this.driver, "geoAreaName");
        mapLocation = new TextEditElement(this.driver, "CapControlSubstation.mapLocationID");
        status = new TrueFalseCheckboxElement(this.driver, "disabled");
        saveBtn = new Button(this.driver, "Save");
        cancelBtn = new Button(this.driver, "Cancel");
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TextEditElement getGeoName() {
        return geoName;
    }
    
    public TextEditElement getMapLocation() {
        return mapLocation;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
