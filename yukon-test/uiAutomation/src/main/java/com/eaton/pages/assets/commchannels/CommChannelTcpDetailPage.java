package com.eaton.pages.assets.commchannels;

import java.util.Optional;

import com.eaton.elements.Section;
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

    public Section getGeneralSection() {
        return new Section(this.driverExt, "General");
    }

    public Section getSharedSection() {
        return new Section(this.driverExt, "Shared");
    }

    public EditTcpCommChannelModal showTcpCommChannelEditModal(String modalTitle) {
        getCommChannelInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-edit-comm-channel-popup");

        return new EditTcpCommChannelModal(this.driverExt, Optional.of(modalTitle), Optional.empty());
    }
}
