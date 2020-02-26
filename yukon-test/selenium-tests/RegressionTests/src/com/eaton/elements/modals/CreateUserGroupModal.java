package com.eaton.elements.modals;

import com.eaton.elements.MultiLineTextElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class UserGroupCreateModal extends BaseModal {

    private DriverExtensions driverExt;
    private TextEditElement name;
    private MultiLineTextElement description;
    
    // TODO no unique way to get modal
    // TODO cancel and save buttons do not have a unique way to select them
    
    public UserGroupCreateModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        this.driverExt = driverExt;
        
        name = new TextEditElement(this.driverExt, "userGroupName", "");
        description = new MultiLineTextElement(this.driverExt, "", "");
    }  
    
    public TextEditElement geName() {
        return name;
    }        
    
    public MultiLineTextElement getDescription() {
        return description;
    }      
}
