package com.eaton.pages.admin.attributes;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.elements.Button;
import com.eaton.elements.Section;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icons;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.attributes.AddAttributeAssignmentsModal;
import com.eaton.elements.modals.attributes.EditAttributeAssignmentsModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class AttributesListPage extends PageBase {
    
    private TextEditElement attributeName;
    private WebTable attrDefTable;
    private WebTable attrAsgmtTable;
    private Section attrDefSection;
    private Section attrAsgmtSection;
    private Button createBtn;
    private Button addBtn;

    public AttributesListPage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Admin.ATTRIBUTES_LIST;

        attrDefSection = new Section(driverExt, "Attribute Definitions");
        attrAsgmtSection = new Section(driverExt, "Attribute Assignments");
        attributeName = new TextEditElement(driverExt, "name", attrDefSection.getSection());
        attrDefTable = new WebTable(driverExt, "compact-results-table", attrDefSection.getSection());
        createBtn = new Button(driverExt, "Create", attrDefSection.getSection());
        addBtn = new Button(driverExt, "Add", attrAsgmtSection.getSection());
        attrAsgmtTable = new WebTable(driverExt, "compact-results-table", attrAsgmtSection.getSection());
    }
    
    public Button getCreateBtn() {
        return createBtn;
    }   
    
    private Button getAddBtn() {
        return addBtn;
    }
    
    public TextEditElement getAttributeName() {
        return attributeName;
    }       
    
    public WebTable getAttrDefTable() {
        return attrDefTable;
    }
    
    public WebTable getAttrAsgmtTable() {
        return attrAsgmtTable;
    }
    
    public Section getAttrAsgmtSection() {
        return attrAsgmtSection;
    }
    
    public Section getAttrDefSection() {
        return attrDefSection;
    }
    
    public AddAttributeAssignmentsModal showAddAttrAsgmtAndWait() {
        getAddBtn().click();
        
        SeleniumTestSetup.waitUntilModalOpenByTitle("Add Attribute Assignment");
        
        return new AddAttributeAssignmentsModal(driverExt, Optional.of("Add Attribute Assignment"), Optional.empty());
    }    
    
    public EditAttributeAssignmentsModal showEditAttrAsgmtAndWaitByIndex(int index) {
        WebTableRow row = getAttrAsgmtTable().getDataRowByIndex(index);
        
        row.showEditModalAndWaitByTitle("Edit Attribute Assignment");
        
        return new EditAttributeAssignmentsModal(this.driverExt, Optional.of("Edit Attribute Assignment"), Optional.empty());
    }        
    
    public void editAttrDefNameByIndex(int index, String value) {
        WebTableRow row = getAttrAsgmtTable().getDataRowByIndex(index);
        
        row.clickGearAndSelectActionByIcon(Icons.PENCIL);
        
        TextEditElement el = new TextEditElement(this.driverExt, "name", row.getCell(0));
        
        el.setInputValue(value);
    }       
}
