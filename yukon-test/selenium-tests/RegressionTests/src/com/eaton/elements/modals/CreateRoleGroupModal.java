package com.eaton.elements.modals;

import com.eaton.elements.MultiLineTextElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;

public class CreateRoleGroupModal extends BaseModal {

    private DriverExtensions driverExt;
    private TextEditElement name;
    private MultiLineTextElement description;
    
    public CreateRoleGroupModal(DriverExtensions driverExt, String modalName) {
        super(driverExt, modalName);
        
        this.driverExt = driverExt;
        
        name = new TextEditElement(this.driverExt, "username", "");
        description = new MultiLineTextElement(this.driverExt, "password.password", "");
    }  
    
    public TextEditElement geName() {
        return name;
    }        
    
    public MultiLineTextElement getDescription() {
        return description;
    }     
}
