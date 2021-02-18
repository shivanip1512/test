package com.eaton.pages.demandresponse.loadgroup;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadGroupModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadGroupDetailPage extends PageBase {
    
    private ActionBtnDropDownElement actionBtn;
    private Section generalSection;
    private Section optionalAttributesSection;
    
    public LoadGroupDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_GROUP_DETAIL + id;
        
        actionBtn = new ActionBtnDropDownElement(this.driverExt);
        generalSection = new Section(this.driverExt, "General");
        optionalAttributesSection = new Section(this.driverExt, "Optional Attributes");
    }
    
    public LoadGroupDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actionBtn;
    }
    
    public Section getGeneralSection() {
        return generalSection;
    }
    
    public Section getOptionalAttributesSection() {
        return optionalAttributesSection;
    }
    
    public ConfirmModal showDeleteLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Delete"); 
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));        
    }
    
    public CopyLoadGroupModal showCopyLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Copy");   
        
        return new CopyLoadGroupModal(this.driverExt, Optional.empty(), Optional.of("copy-loadGroup-popup")); 
    }          
}