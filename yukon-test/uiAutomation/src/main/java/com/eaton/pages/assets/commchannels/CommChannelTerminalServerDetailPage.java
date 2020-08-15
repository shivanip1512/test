package com.eaton.pages.assets.commchannels;

import java.util.Optional;

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

    public EditTerminalServerCommChannelModal showTerminalServerCommChannelEditModal() {
        getCommChannelInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-edit-comm-channel-popup");

        return new EditTerminalServerCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-edit-comm-channel-popup"));
    }
}
