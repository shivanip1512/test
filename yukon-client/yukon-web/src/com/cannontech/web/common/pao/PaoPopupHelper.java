package com.cannontech.web.common.pao;

import org.springframework.ui.ModelMap;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.user.YukonUserContext;

public interface PaoPopupHelper {

    /**
     * Takes a device collection and adds the appropriate attributes to the
     * model to build a 'selected devices' popup.
     */
    public String buildPopupModel(DeviceCollection collection, ModelMap model,
            YukonUserContext context);

}
