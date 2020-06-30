package com.eaton.pages.assets.commChannels;

import java.util.Optional;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.CreateCommChannelModal;
import com.eaton.elements.modals.EditCommChannelModal;
import com.eaton.elements.panels.CommChannelInfoPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class CommChannelDetailPage extends PageBase {

    public CommChannelDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNEL_DETAIL+id;
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
    
    public String getCommChannelInfoPanelText() {
        CommChannelInfoPanel infoPanel= new CommChannelInfoPanel(this.driverExt, "Comm Channel Information");
        return infoPanel.getPanelNameText();
    }

    public EditCommChannelModal showCommChannelEditModal(Optional<String> modalTitle) {

        getCommChannelInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-edit-comm-channel-popup");

        return new EditCommChannelModal(this.driverExt, modalTitle, Optional.of("js-edit-comm-channel-popup"));
    }    
    
    public ConfirmModal showDeleteLoadGroupModal() {
        getActionBtn().clickAndSelectOptionByText("Delete"); 
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new ConfirmModal(this.driverExt, Optional.empty(), Optional.of("yukon_dialog_confirm"));        
    }
    
    public CreateCommChannelModal showCreateCommChannelModal(String modalName) {
        getActionBtn().clickAndSelectOptionByText("Create"); 
        
        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("yukon_dialog_confirm");
        
        return new CreateCommChannelModal(this.driverExt, modalName);
    }
}
