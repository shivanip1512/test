package com.eaton.pages.admin.attributes;

import java.util.Optional;

import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.editwebtable.EditWebTable;
import com.eaton.elements.editwebtable.EditWebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.attributes.AddAttributeAssignmentsModal;
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
        return new Button(driverExt, "Add", getAttrAsgmtSection().getSection());
    }
    
    public TextEditElement getAttributeName() {
        return new TextEditElement(driverExt, "name", getAttrDefSection().getSection());
    }       
    
    public EditWebTable getAttrDefTable() {
        return new EditWebTable(driverExt, "compact-results-table", getAttrDefSection().getSection());
    }
    
    public EditWebTable getAttrAsgmtTable() {
        return new EditWebTable(driverExt, "compact-results-table", getAttrAsgmtSection().getSection());
    }
    
    public Section getAttrAsgmtSection() {
        return new Section(driverExt, "Attribute Assignments");
    }
    
    public Section getAttrDefSection() {
        return new Section(driverExt, "Attribute Definitions");
    }
    
    public AddAttributeAssignmentsModal showAddAttrAsgmtAndWait() {
        getAddBtn().click();
        
        SeleniumTestSetup.waitUntilModalOpenByTitle("Add Attribute Assignment");
        
        return new AddAttributeAssignmentsModal(driverExt, Optional.of("Add Attribute Assignment"), Optional.empty());
    }             
    
    public TextEditElement editAttributeDefByNameAndClickSave(String name, String value) {
        EditWebTableRow row = getAttrDefTable().getDataRowByName(name);
        
        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
        
        TextEditElement el = new TextEditElement(this.driverExt, "name", row.getCellByIndex(1));
        
        el.setInputValue(value);
        
        row.clickSave();        
        
        return el;
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
}
