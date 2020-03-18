package com.eaton.pages.ami;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.EditMeterModal;
import com.eaton.elements.panels.MeterInfoPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class MeterDetailsPage extends PageBase {

    public MeterDetailsPage(DriverExtensions driverExt, String pageUrl) {
        super(driverExt, pageUrl);

    }
    
    public ActionBtnDropDownElement getAction() {
        return new ActionBtnDropDownElement(this.driverExt);
    }

    public MeterInfoPanel getMeterInfoPanel() {
        return new MeterInfoPanel(this.driverExt, "Meter Info");
    }

    public EditMeterModal showMeterEditModal() {

        getMeterInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("meter-info-popup");

        return new EditMeterModal(this.driverExt, Optional.empty(), Optional.of("meter-info-popup"));
    }    
    
    public ConfirmModal showAndWaitConfirmDeleteModal() {
        
        getAction().clickAndSelectOptionByText("Delete Meter");       
                      
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));  
    }
}
