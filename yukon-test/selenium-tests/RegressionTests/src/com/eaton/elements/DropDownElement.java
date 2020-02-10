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

    public DropDownElement(WebDriver driver, String elementName, String parentName) {

        this.driver = driver;
        this.elementName = elementName;
        this.parentName = parentName;
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
    
    private WebElement getSelectElement() {
        if (this.parentName != null) {
            return this.driver.findElement(By.cssSelector("[aria-describedby='" + this.parentName + "'] select[name='" + this.elementName + "']"));
        } else {
            return this.driver.findElement(By.cssSelector("select[name='" + this.elementName + "']"));   
        }        
    }
}
