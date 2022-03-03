package com.eaton.pages.admin.attributes;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownMultiSelectElement;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.editwebtable.EditWebTable;
import com.eaton.elements.editwebtable.EditWebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.attributes.AddAttributeAssignmentsModal;
import com.eaton.elements.modals.attributes.EditAttributeAssignmentsModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AttributesListPage extends PageBase {
    
    public AttributesListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ATTRIBUTES_LIST;
    }
    
    public Button getCreateBtn() {
        return new Button(driverExt, "Create", getAttrDefSection().getSection());
    }   
    
    private Button getAddBtn() {
        return new Button(driverExt, "Add");
    }
    
    public TextEditElement getAttributeName() {
        return new TextEditElement(driverExt, "name", getAttrDefSection().getSection());
    }       
    
    public EditWebTable getAttrDefTable() {
        return new EditWebTable(driverExt, "compact-results-table", getAttrDefSection().getSection());
    }
    
    public WebTable getAttrAsgmtTable() {
        return new WebTable(driverExt, "compact-results-table", getAttrAsgmtSection().getSection());
    }
    
    public Section getAttrAsgmtSection() {
        return new Section(driverExt, "Attribute Assignments");
    }
    
    public Section getAttrDefSection() {
        return new Section(driverExt, "Attribute Definitions");
    }
    
    public DropDownMultiSelectElement getFilterByAttr() {
        return new DropDownMultiSelectElement(driverExt, "selectedAttributes");
    }
    
    public DropDownMultiSelectElement getFilterByDeviceTypes() {
        return new DropDownMultiSelectElement(driverExt, "selectedDeviceTypes");
    }
    
    public Button getFilterBtn() {
        return new Button(driverExt, "Filter");
    }
    
    public AddAttributeAssignmentsModal showAddAttrAsgmtAndWait() {
        getAddBtn().click();
        
        SeleniumTestSetup.waitUntilModalOpenByTitle("Add Attribute Assignment");
        
        return new AddAttributeAssignmentsModal(driverExt, Optional.of("Add Attribute Assignment"), Optional.empty());
    }   
    
    public EditAttributeAssignmentsModal showEditAttrAsgmtAndWait(String name) {
        WebTableRow row = getAttrAsgmtTable().getDataRowByName(name);
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        SeleniumTestSetup.waitUntilModalOpenByTitle("Edit Attribute Assignment");
        
        return new EditAttributeAssignmentsModal(driverExt, Optional.of("Edit Attribute Assignment"), Optional.empty());
    } 
    
    public ConfirmModal showDeleteAttrAsgmtAndWait(String name) {
        WebTableRow row = getAttrAsgmtTable().getDataRowByName(name);
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.REMOVE);
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    } 
    
    public void editAttributeDefNameClickSaveAndWait(String attrName, String value) {        
        EditWebTableRow row = getAttrDefTable().getDataRowByName(attrName);   
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        TextEditElement el = new TextEditElement(this.driverExt, "name", row.getCellByIndex(1));
        
        el.setInputValue(value);
        
        row.clickSaveAndWait();                
    }   
    
    public void editAttributeDefNameAndClickCancelAndWait(String attrName, String value) {        
        EditWebTableRow row = getAttrDefTable().getDataRowByName(attrName);   
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        TextEditElement el = new TextEditElement(this.driverExt, "name", row.getCellByIndex(1));
        
        el.setInputValue(value);
        
        row.clickCancelAndWait();                
    }  
    
    public void editAttributeDefNameAndClickSave(String attrName, String value) {        
        EditWebTableRow row = getAttrDefTable().getDataRowByName(attrName);   
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        TextEditElement el = new TextEditElement(this.driverExt, "name", row.getCellByIndex(1));
        
        el.setInputValue(value);
        
        row.clickSave();                
    } 
    
    public TextEditElement waitForSave(String id) {
        boolean staleElement = true;
        TextEditElement te = null;
        WebElement r = null;
        long startTime = System.currentTimeMillis();
        
        while(staleElement && (System.currentTimeMillis() - startTime < 3000)) {
            try {
                EditWebTable table = getAttrDefTable();
                r = table.getEditDataRowBySpanClassName("attribute-" + id);                                
                te = new TextEditElement(this.driverExt, "name", r);
                
                staleElement = false;
                
            } catch(StaleElementReferenceException ex) {
            }
        }
        
        return te;        
    }
    
    public TextEditElement editAttributeDefByName(String name) {
        EditWebTableRow row = getAttrDefTable().getDataRowByName(name);                
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        return new TextEditElement(this.driverExt, "name", row.getCellByIndex(1));
    } 
    
    public ConfirmModal showDeleteAttrDefByName(String name) {
        EditWebTableRow row = getAttrDefTable().getDataRowByName(name);      
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.REMOVE);
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));       
    }   
    
    public String getAttrAsgmtErrorMsg() {
        String msg = "";
        long startTime = System.currentTimeMillis();
        
        while(msg.equals("") && (System.currentTimeMillis() - startTime < 3000)) {
            try {
                WebElement sec = getAttrAsgmtSection().getSection();
                
                List<WebElement> list = sec.findElements(By.cssSelector("#user-message"));
                
                //el = list.stream().filter(x -> x.getCssValue("display").contains("none")).findFirst().orElseThrow();
                
                for (WebElement element : list) {
                    String display = element.getCssValue("display");
                    if (!display.equals("none")) {
                        msg = element.getText();
                    }
                }
                
            } catch(StaleElementReferenceException ex) {
            }
        }
        
        return msg;
    }
}
