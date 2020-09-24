package com.eaton.elements;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.modals.BaseModal;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.attributes.EditAttributeAssignmentsModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class WebTableRow{

    private WebElement row;
    private DriverExtensions driverExt;

    public WebTableRow(DriverExtensions driverExt, WebElement row) {
        this.driverExt = driverExt;
        this.row = row;
    }

    public WebElement getCell(int cellIndex) {

        return this.row.findElement(By.cssSelector("tbody tr>td:nth-child(" + cellIndex + ")"));
    } 
    
    public void selectCellByLink() {
        this.row.findElement(By.cssSelector("a")).click();        
    }
    
    public String getCellLinkByIndex(int index) {
        List<WebElement> cells = this.row.findElements(By.cssSelector("td"));
        return cells.get(index).findElement(By.cssSelector("a")).getAttribute("href");
    }
    
    public void clickActionIcon(Icons icon) {
        this.row.findElement(By.cssSelector("." + icon.getIcon())).click();
    }
    
    public void clickGearAndSelectActionByIcon(Icons icon) {
        List<WebElement> list = this.driverExt.findElements(By.cssSelector(".dropdown-menu"), Optional.of(1));
        
        WebElement el = list.stream().filter(x -> x.getAttribute("style").contains("disiplay: block;")).findFirst().orElseThrow();
        
        el.findElement(By.cssSelector(icon.getIcon())).click();
    }
    
    public ConfirmModal showDeleteModalAndWait() {
        clickGearAndSelectActionByIcon(Icons.DELETE);
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    }
    
    public void showEditModalAndWaitByTitle(String title) {
        clickGearAndSelectActionByIcon(Icons.PENCIL);
        
        SeleniumTestSetup.waitUntilModalOpenByTitle(title);        
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
