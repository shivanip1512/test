package com.eaton.pages.capcontrol;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.pages.PageBase;

public class CapBankCreatePage  extends PageBase {
    
    private TextEditElement name;
    private TrueFalseCheckboxElement status;
    private TrueFalseCheckboxElement createNewCbc;
    private Button saveBtn;
    private Button cancelBtn;

    public CapBankCreatePage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(driver, "name");
        status = new TrueFalseCheckboxElement(driver, "disabled");
        createNewCbc = new TrueFalseCheckboxElement(driver, "createCBC");
        saveBtn = new Button(driver, "Save");
        cancelBtn = new Button(driver, "Cancel");
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
    
    public TrueFalseCheckboxElement getCreateNewCbc() {
        return createNewCbc;
    }
    
    public Button getSaveBtn() {
        return saveBtn;
    }
    
    public Button getCancelBtn() {
        return cancelBtn;
    }    
}
