package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.elements.WebTableRow.Icons;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class WebTableRow{

    private WebElement row;
    private DriverExtensions driverExt;

    public WebTableRow(DriverExtensions driverExt, WebElement row) {
        this.driverExt = driverExt;
        this.row = row;
    }
    
    public WebElement getCellByIndex(int index) {
        return this.row.findElement(By.cssSelector("td:nth-child(" + index + ")"));
    }
    
    public void selectCellByLink() {
        this.row.findElement(By.cssSelector("a")).click();        
    }
    
    public String getCellLinkByIndex(int index) {
        List<WebElement> cells = this.row.findElements(By.cssSelector("td"));
        return cells.get(index).findElement(By.cssSelector("a")).getAttribute("href");
    }
    
    public String getCellTextByNthChild(int index) {
        return this.row.findElement(By.cssSelector("td:nth-child(" + index + ")")).getText();
    }
    
    public String getCellLinkTextByIndex(int index) {
        List<WebElement> cells = this.row.findElements(By.cssSelector("td"));
        return cells.get(index).findElement(By.cssSelector("a")).getText();
    }
    
    public void clickActionIcon(Icons icon) {
        this.row.findElement(By.cssSelector("." + icon.getIcon())).click();
    }
    
    public void clickGearAndSelectActionByIcon(Icons icon) {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".dropdown-menu"), Optional.of(1));
        
        WebElement el = list.stream().filter(x -> x.getAttribute("style").contains("disiplay: block;")).findFirst().orElseThrow();
        
        el.findElement(By.cssSelector(icon.getIcon())).click();
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
    
    public enum Icons {
        COG("icon-cog"),
        PENCIL("icon-pencil"),
        REMOVE("icon-cross"),
        NOTES("icon-notes-pin"),
        DELETE("icon-delete"),
        SAVE("icon-disk");
        

        private final String icon;

        Icons(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return this.icon;
        }
    }
}
