package com.eaton.pages.admin.attributes;

import java.util.Optional;

import com.eaton.elements.Button;
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
    
//    public EditAttributeAssignmentsModal showEditAttrAsgmtAndWaitByIndex(int index) {
//        WebTableRow row = getAttrAsgmtTable().getDataRowByIndex(index);
//        
//        row.showEditModalAndWaitByTitle("Edit Attribute Assignment");
//        
//        return new EditAttributeAssignmentsModal(this.driverExt, Optional.of("Edit Attribute Assignment"), Optional.empty());
//    }        
    
//    public void editAttrDefNameByIndex(int index, String value) {
//        WebTableRow row = getAttrAsgmtTable().getDataRowByIndex(index);
//        
//        row.hoverAndClickGearAndSelectActionByIcon(Icons.PENCIL);
//        
//        TextEditElement el = new TextEditElement(this.driverExt, "name", row.getCell(0));
//        
//        el.setInputValue(value);
//    }     
    
    public ConfirmModal showDeleteAttrDefByName(String name) {
        EditWebTableRow row = getAttrDefTable().getDataRowByName(name);      
        
        row.showDeleteModalAndWait();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");

        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));
    }
}
