package com.eaton.pages.assets.commchannels;

import java.util.Optional;

import com.eaton.elements.modals.commchannel.EditTcpCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class CommChannelTcpDetailPage extends CommChannelDetailPage {

    public CommChannelTcpDetailPage(DriverExtensions driverExt, int id) {
        super(driverExt);

    requiresLogin = true;
    pageUrl = Urls.Assets.COMM_CHANNEL_DETAIL + id;
    }

    public EditTcpCommChannelModal showTcpCommChannelEditModal() {
        getCommChannelInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("js-edit-comm-channel-popup");

        return new EditTcpCommChannelModal(this.driverExt, Optional.empty(), Optional.of("js-edit-comm-channel-popup"));
    }
}
