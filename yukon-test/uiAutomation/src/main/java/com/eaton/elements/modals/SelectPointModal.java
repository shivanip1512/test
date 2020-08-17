package com.eaton.elements.modals;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class SelectPointModal extends BaseModal {
    
    public SelectPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);       
    }
    
    public WebTable getPointTable() {
        return new WebTable(driverExt, "compact-results-table", getModal());
    }
    

    /**
     * @param pointName = pass in the name of the point
     * @param id = optional field, use if name is not unique, otherwise pass in Optional.empty()
     */
    public void selectPoint(String pointName, Optional<String> id) {
        if(id.isPresent()) {
            getPointTable().searchTable(id.get());
        } else {
            getPointTable().searchTable(pointName);    
        }                
        
        WebTable table = getPointTable();
        WebTableRow row = table.getDataRowByName(pointName);

        row.selectCellByLink();
    }

    public void clickOK() {
        getSelectPointModal().findElement(By.cssSelector(".ui-dialog-buttonset .primary")).click();

        SeleniumTestSetup.waitUntilModalClosedByTitle("Select Point");
    }

    public WebElement getSelectPointModal() {

        Optional<WebElement> found = Optional.empty();

        long startTime = System.currentTimeMillis();

        while (found.isEmpty() && System.currentTimeMillis() - startTime < 3000) {

            List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".ui-dialog"), Optional.of(5));

            found = elements.stream()
                    .filter(element -> element.findElement(By.cssSelector(".ui-dialog-title")).getText().equals("Select Point"))
                    .findFirst();
        }

        return found.get();
    }
}
