package com.eaton.pages.assets.commchannels;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.elements.modals.EditCommChannelModal;
import com.eaton.elements.panels.CommChannelInfoPanel;
import com.eaton.elements.tabs.TabElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CommChannelDetailPage extends PageBase {
    
    public CommChannelDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNEL_DETAIL + id;
    }
    
    public CommChannelDetailPage(DriverExtensions driverExt) {
        super(driverExt);
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return new ActionBtnDropDownElement(this.driverExt);
    }
    
    public CommChannelInfoPanel getCommChannelInfoPanel() {
        return new CommChannelInfoPanel(this.driverExt, "Comm Channel Information");
    }
    
    public TabElement getTabElement() {
        return new TabElement(this.driverExt);
    }
    
    public Section getTimingSection() {
        return new Section(this.driverExt, "Timing");
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General");
    }
    
    public Section getSharedSection() {
        return new Section(this.driverExt, "Shared");
    }

    public EditCommChannelModal showCommChannelEditModal(String modalTitle) {
        getCommChannelInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-edit-comm-channel-popup");

        return new EditCommChannelModal(this.driverExt, Optional.of(modalTitle), Optional.of("js-edit-comm-channel-popup"));
    }    
    
    public ConfirmModal showDeleteLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Delete"); 
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));        
    }
    
    public CreateCommChannelModal showCreateCommChannelModal() {
    	getActionBtn().clickAndSelectOptionByText("Create");
    	 
    	SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-create-comm-channel-popup");

    	return new CreateCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-create-comm-channel-popup"));
    }
}
