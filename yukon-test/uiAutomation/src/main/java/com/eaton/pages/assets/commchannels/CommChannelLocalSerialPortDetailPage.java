package com.eaton.pages.assets.commchannels;

import java.util.Optional;

import com.eaton.elements.Section;
import com.eaton.elements.modals.commchannel.EditLocalSerialPortCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class CommChannelLocalSerialPortDetailPage extends CommChannelDetailPage {
    
    private Section generalSection;
    private Section sharedSection;

    public CommChannelLocalSerialPortDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        generalSection = new Section(this.driverExt, "General");
        sharedSection = new Section(this.driverExt, "Shared");

        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNEL_DETAIL + id;
    }
    
    public Section getGeneralSection() {
        return generalSection;
    }

    public Section getSharedSection() {
        return sharedSection;
    }

    public EditLocalSerialPortCommChannelModal showLocalSerialPortCommChannelEditModal() {
        String describedBy = "js-edit-comm-channel-popup";
        getEditBtn().getButton().click();

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy(describedBy);

        return new EditLocalSerialPortCommChannelModal(this.driverExt, Optional.empty(), Optional.of(describedBy));
    }
}
