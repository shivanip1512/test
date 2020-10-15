package com.eaton.elements.modals.attributes;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.modals.BaseModal;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class AttributeAssignmentsModal extends BaseModal {
    
    private DropDownElement attributeName;
    private DropDownElement pointType;
    private TextEditElement pointOffset;    

    public AttributeAssignmentsModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        attributeName = new DropDownElement(driverExt, "attributeId", getModal());
        pointType = new DropDownElement(driverExt, "pointType", getModal());
        pointOffset = new TextEditElement(driverExt, "offset", getModal());
    }

    public DropDownElement getAttributeName() {
        return attributeName;
    }
    
    public DropDownElement getPointType() {
        return pointType;
    }
    
    public TextEditElement getpointOffSet() {
        return pointOffset;
    }
    
    public SelectPointModal clickSearchAndWait() {
        getModal().findElement(By.cssSelector(".icon.icon-magnifier")).click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("displayPointPicker");
        
        return new SelectPointModal(driverExt, Optional.empty(), Optional.of("displayPointPicker"));
        
    }
}
