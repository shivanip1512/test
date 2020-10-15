package com.eaton.elements.editwebtable;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.elements.WebTableRow.Icons;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class EditWebTableRow {

    private WebElement row;
    private DriverExtensions driverExt;

    public EditWebTableRow(DriverExtensions driverExt, WebElement row) {
        this.driverExt = driverExt;
        this.row = row;        
    }
    
    public void hoverAndClickGearAndSelectActionByIcon(Icons icon) {
        Actions action = new Actions(this.driverExt.getDriver());
        WebElement we = this.row.findElement(By.cssSelector(".dropdown-trigger"));
        action.moveToElement(we).moveToElement(this.row.findElement(By.cssSelector(".icon.icon-cog"))).click().build().perform();
        SeleniumTestSetup.waitUntilDropDownMenuOpen();
        
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".dropdown-menu"), Optional.of(1));
        
        WebElement el = list.stream().filter(x -> x.getAttribute("style").contains("display: block;")).findFirst().orElseThrow();
        
        el.findElement(By.cssSelector(".dropdown-option ." + icon.getIcon())).click();
    }     
    
    public WebElement getCellByIndex(int index) {        
        return this.row.findElement(By.cssSelector("td:nth-child(" + index + ")"));
    } 
          
    public void clickSave() {
        this.row.findElement(By.cssSelector("td:nth-child(1) .button-group ." + Icons.SAVE.getIcon())).click();                
    }
    
    public void clickCancel() {
        this.row.findElement(By.cssSelector("td:nth-child(1) .button-group ." + Icons.DELETE.getIcon())).click();
    }
}
