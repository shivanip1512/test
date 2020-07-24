package com.eaton.pages.assets.commchannels;

import java.util.Optional;

import com.eaton.elements.Section;
import com.eaton.elements.modals.commchannel.EditLocalSerialPortCommChannelModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;

public class CommChannelLocalSerialPortDetailPage extends CommChannelDetailPage{

    public CommChannelLocalSerialPortDetailPage(DriverExtensions driverExt, int id) {
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

    public EditLocalSerialPortCommChannelModal showLocalSerialPortCommChannelEditModal(String modalTitle) {
        getCommChannelInfoPanel().getEdit().click();

        SeleniumTestSetup.waitUntilModalVisibleByDescribedBy("js-edit-comm-channel-popup");

        return new EditLocalSerialPortCommChannelModal(this.driverExt, Optional.of(modalTitle), Optional.empty());
    }
}
