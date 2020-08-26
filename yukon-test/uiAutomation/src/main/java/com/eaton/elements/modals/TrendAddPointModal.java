package com.eaton.elements.modals;

import java.util.Optional;

import com.eaton.elements.DropDownElement;
import com.eaton.elements.PickerElement;
import com.eaton.elements.RadioButtonElement;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;

public class TrendAddPointModal extends BaseModal {   

    private String modalTitle;
    private String desrcibedBy;
    
    public TrendAddPointModal(DriverExtensions driverExt, Optional<String> modalTitle, Optional<String> describedBy) {
        super(driverExt, modalTitle, describedBy);
        
        if (modalTitle.isPresent()) {
            this.modalTitle = modalTitle.get();
        }
        
        if (describedBy.isPresent()) {
            this.desrcibedBy = describedBy.get();
        }
    }   
    
    public PickerElement getPoint() {
        return new PickerElement(this.driverExt, "picker-trendPointPicker");
    }
    
    public TextEditElement getLabel() {
        return new TextEditElement(this.driverExt, "label", getModal());
    }
    
    public DropDownElement getStyle() {
        return new DropDownElement(this.driverExt, "style", getModal());
    }
    
    public DropDownElement getType() {
        return new DropDownElement(this.driverExt, "type", getModal());
    }
    
    public RadioButtonElement getAxis() {
        return new RadioButtonElement(this.driverExt, "axis", getModal());
    }
    
    public TextEditElement getMultiplier() {
        return new TextEditElement(this.driverExt, "multiplier", getModal());
    }
    
    public SelectPointModal showAndWaitSelectPointModal() {
        getPoint().clickLinkWithDynamicId();
        
        SeleniumTestSetup.waitUntilModalVisibleByTitle("Select Point");
        
        return new SelectPointModal(this.driverExt, Optional.of("Select Point"), Optional.empty());
    }
}
