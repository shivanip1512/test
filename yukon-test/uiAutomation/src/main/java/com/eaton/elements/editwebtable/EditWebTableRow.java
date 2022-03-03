package com.eaton.elements.editwebtable;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
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
    
    public String getCellTextByIndex(int index) {
        WebElement cell = this.row.findElement(By.cssSelector("td:nth-child(" + index + ")"));
        
        WebElement el = cell.findElement(By.cssSelector("js-view-*"));
        
        return el.getText();
    }
          
    public void clickSaveAndWait() {
        this.row.findElement(By.cssSelector("td:nth-child(1) .button-group ." + Icons.SAVE.getIcon())).click();  
        
        long startTime = System.currentTimeMillis();
        String style = "none";
        
        while(style.equals("none") && (System.currentTimeMillis() - startTime < 3000)) {
            try {
                WebElement editRow = this.row.findElement(By.cssSelector("td:nth-child(1) span[class*='js-view']"));
                
                style = editRow.getCssValue("display");
                
            } catch(StaleElementReferenceException ex) {
            }
        }
    }
    
    public void clickSave() {
        this.row.findElement(By.cssSelector("td:nth-child(1) .button-group ." + Icons.SAVE.getIcon())).click(); 
    }
    
    public void clickCancelAndWait() {
        this.row.findElement(By.cssSelector("td:nth-child(1) .button-group ." + Icons.DELETE.getIcon())).click();
        
        WebElement editRow = this.row.findElement(By.cssSelector("td:nth-child(1) span[class*='js-view']"));
        
        String style = editRow.getCssValue("display");
        
        long startTime = System.currentTimeMillis();
        
        while(style.equals("none") && (System.currentTimeMillis() - startTime < 2000)) {
            
            style = this.row.getAttribute("style");
        }
    }    
    
//    public boolean isEditable() {
//        String style = this.row.getAttribute("style");
//        
//    }
}
