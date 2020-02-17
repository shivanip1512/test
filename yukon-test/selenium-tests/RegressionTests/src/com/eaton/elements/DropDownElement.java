package com.eaton.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DropDownElement {
    
    private WebDriver driver;
    private String elementName;
    private String parentName;
    private WebElement parentElement;
    private WebElement selectElement;

    public DropDownElement(WebDriver driver, String elementName) {
        this.driver = driver;
        this.elementName = elementName;
        
        setSelectElement();
    }
    
    public DropDownElement(WebDriver driver, String elementName, String parentName) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
        
        setSelectElement();
    }
    
    public DropDownElement(WebDriver driver, String elementName, WebElement parentElement) {
        this.driver = driver;
        this.elementName = elementName;
        this.parentElement = parentElement;
        
        setSelectElement();
    }
    
    public void selectItemByText(String text)
    {
        if (text.isBlank())
        {
            return;
        }

        Select dropDown = new Select(getSelectElement());
        
        dropDown.selectByVisibleText(text);
    }
    
    public void selectItemByIndex(int index)
    {
        new Select(getSelectElement()).selectByIndex(index);
    }
    
    public int getOptionCount() {
        List<WebElement> options = getSelectElement().findElements(By.tagName("option"));
        
        return options.size();
    }
    
    private void setSelectElement() {
        if (this.parentName != null) {
            this.selectElement = this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] select[name='" + this.elementName + "']"));
        } else if (this.parentElement != null) {
            this.selectElement = this.parentElement.findElement(By.cssSelector("select[name='" + this.elementName + "']"));
        } else {
            this.selectElement = this.driver.findElement(By.cssSelector("select[name='" + this.elementName + "']"));   
        }        
    }  
    
    public WebElement getSelectElement() {
        return selectElement;
    }
}
