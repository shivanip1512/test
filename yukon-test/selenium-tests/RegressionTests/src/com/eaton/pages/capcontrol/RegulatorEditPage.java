package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.WebTable;
import com.eaton.pages.PageBase;

public class RegulatorEditPage extends PageBase {
    
    private TextEditElement name;
    private TextEditElement description;
    private DropDownElement type;
    private DropDownElement configuration;
    private TrueFalseCheckboxElement status;
    private WebTable table;

    public RegulatorEditPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name");
        description = new TextEditElement(this.driver, "description");
        type = new DropDownElement(this.driver, "type");
        configuration = new DropDownElement(this.driver, "configId");
        status = new TrueFalseCheckboxElement(this.driver, "disabled");
        table = new WebTable(this.driver, "compact-results-table");
    }

    public String getPageTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }
    
    public TextEditElement getName() {
        return name;
    }
    
    public TextEditElement getDescription() {
        return description;
    }
    
    public DropDownElement getType() {
        return type;
    }
    
    public DropDownElement getConfiguration() {
        return configuration;
    }
    
    public TrueFalseCheckboxElement getStatus() {
        return status;
    }
    
    public WebTable getAttributeMappingsTable() {
        return table;
    }
    
    public Button getSaveBtn() {
        return new Button(this.driver, "Save");
    }
    
    public Button getCancelBtn() {
        return new Button(this.driver, "Cancel");
    }   
    
    public Button getDeleteBtn() {
        return new Button(this.driver, "Delete");
    }
}
