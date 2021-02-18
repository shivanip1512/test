package com.eaton.pages.assets.commchannels;

import java.util.Optional;

import com.eaton.elements.Section;
import com.eaton.elements.modals.commchannel.EditTerminalServerCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class CommChannelTerminalServerDetailPage extends CommChannelDetailPage {

    public CommChannelTerminalServerDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.Assets.COMM_CHANNEL_DETAIL + id;
    }
    
    public Section getGeneralSection() {
        return new Section(this.driverExt, "General");
    }

    public Section getSharedSection() {
        return new Section(this.driverExt, "Shared");
    }

    public EditTerminalServerCommChannelModal showTerminalServerCommChannelEditModal() {
        String describedBy = "js-edit-comm-channel-popup";
        getEditBtn().getButton().click();

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy(describedBy);

        return new EditTerminalServerCommChannelModal(this.driverExt, Optional.empty(), Optional.of(describedBy));
    }
}
