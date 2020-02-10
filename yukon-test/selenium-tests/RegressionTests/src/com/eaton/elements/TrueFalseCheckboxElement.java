package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TrueFalseCheckboxElement {

    WebDriver driver;
    String elementName;
    String parentName;
    
    public TrueFalseCheckboxElement(WebDriver driver, String elementName, String parentName) {
        
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
    }
    
    public void setValue(Boolean value) {
        
        Boolean checked = isChecked();
        
        if (!value.equals(checked)) {
            getCheckbox().click();
        }        
    }
    
    private Boolean isChecked() {
         String checked = getCheckbox().getAttribute("checked");
         
         return checked.equals("checked");
    }
    
    private WebElement getCheckbox() {
        if(parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("input[name = '" + this.elementName + "']"));   
        }        
    }
}
