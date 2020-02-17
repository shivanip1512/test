package com.eaton.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TrueFalseCheckboxElement {

    private WebDriver driver;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement checkbox;
    
    public TrueFalseCheckboxElement(WebDriver driver, String elementName) {        
        this.driver = driver;
        this.elementName = elementName;
        
        setCheckbox();
    }
    
    public TrueFalseCheckboxElement(WebDriver driver, String elementName, String parentName) {        
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setCheckbox();
    }
    
    public TrueFalseCheckboxElement(WebDriver driver, String elementName, WebElement parentElement) {        
        this.driver = driver;
        this.elementName = elementName;
        this.parentElement = parentElement;        
        
        setCheckbox();
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
    
    private void setCheckbox() {
        if(this.parentName != null) {
            this.checkbox = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] input[name = '" + this.elementName + "']"));
        } else if (this.parentElement != null) {
            this.checkbox = this.parentElement.findElement(By.cssSelector("input[name = '" + this.elementName + "']"));
        } else {
            this.checkbox = this.driver.findElement(By.cssSelector("input[name = '" + this.elementName + "']"));   
        }        
    }
    
    private WebElement getCheckbox() {
        return checkbox;
    }
}
