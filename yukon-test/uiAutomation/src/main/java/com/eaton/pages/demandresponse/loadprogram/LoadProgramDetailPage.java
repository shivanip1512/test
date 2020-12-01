package com.eaton.pages.demandresponse.loadprogram;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.WebTable;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CopyLoadProgramModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.elements.tabs.MemberControlTab;
import com.eaton.elements.tabs.NotificationTab;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadProgramDetailPage extends PageBase {

    public LoadProgramDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_PROGRAM_DETAILS + id;
    }
    
    public LoadProgramDetailPage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public ConfirmModal showDeleteLoadProgramModal() {
        getActionBtn().clickAndSelectOptionByText("Delete");
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));        
    }
    
    public CopyLoadProgramModal showCopyLoadProgramModal() {
        getActionBtn().clickAndSelectOptionByText("Copy");   
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("copy-loadProgram-popup");
        
        return new CopyLoadProgramModal(this.driverExt, Optional.empty(), Optional.of("copy-loadProgram-popup")); 
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General");
    }
    
    public Section getTrgThresholdSection() {
        return new Section(this.driverExt, "Trigger Threshold Settings");
    }
    
    public Section getGearsSection() {
        return new Section(this.driverExt, "Gears");
    }
    
    public WebTable getGearsSectionTable() {
        return new WebTable(this.driverExt, "compact-results-table", getGearsSection().getSection());
    }
    
    public Section getControlWindowSection() {
        return new Section(this.driverExt, "Control Window");
    }
    
    public TabElement getAllTabs() {
        return new TabElement(this.driverExt);
    }
    
    public LoadGroupsTab getLoadGroupTab() {
        return new LoadGroupsTab(this.driverExt);
    }
    
    public WebTable getLoadGroupTable() {
        return new WebTable(this.driverExt, "compact-results-table", getLoadGroupTab().getTabPanelByName("Load Groups"));
    }
    
    public NotificationTab getNotificationTab() {
        return new NotificationTab(this.driverExt);
    }
    
    public MemberControlTab getMemberControl() {
        return new MemberControlTab(this.driverExt);
    }
}