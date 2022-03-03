package com.eaton.elements.modals;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.framework.DriverExtensions;


public class SelectPointModal extends BaseModal {
    
    /**
     * @param driverExt
     * @param modalTitle
     * @param describedBy
     */
    public SelectPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);       
    }
    
    public WebTable getPointTable() {
        return new WebTable(driverExt, "compact-results-table", getModal());
    }
    

    /**
     * Use this method when you can search by name or id, but only use id if absolutely necessary.
     * 
     * @param pointName the name of the point, that will be clicked on to select
     * @param id = optional field, id of the point, otherwise use Optional.empty()
     */
    public void selectPoint(String pointName, Optional<String> id) {
        if(id.isPresent()) {
            getPointTable().searchTable(id.get());
        } else {
            getPointTable().searchTable(pointName);    
        }                
        
        WebTable table = getPointTable();
        WebTableRow row = table.getDataRowByLinkName(pointName);

        row.selectCellByLink();
    }    
    
    
    
    /**
     * Use this method only when you need to select a point, and can only search by name.  
     * Id will be used to help select the correct result if multiple values are returned.
     * 
     * @param pointName the name of the point, that will be clicked on to select
     * @param id the id of the point
     */
    public void selectPointById(String pointName, String id) {
        WebElement row = getPointTable().searchAndGetRowById(pointName, id);
        row.click();
    }
}
