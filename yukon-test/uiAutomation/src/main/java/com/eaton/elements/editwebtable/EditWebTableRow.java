package com.eaton.elements.editwebtable;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.modals.ConfirmModal;
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
        
        WebElement el = list.stream().filter(x -> x.getAttribute("style").contains("disiplay: block;")).findFirst().orElseThrow();
        
        el.findElement(By.cssSelector(icon.getIcon())).click();
    }
    
    public ConfirmModal showDeleteModalAndWait() {
        hoverAndClickGearAndSelectActionByIcon(Icons.DELETE);
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    }
    
    public void showEditModalAndWaitByTitle(String title) {
        hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        SeleniumTestSetup.waitUntilModalOpenByTitle(title);        
    } 
}
