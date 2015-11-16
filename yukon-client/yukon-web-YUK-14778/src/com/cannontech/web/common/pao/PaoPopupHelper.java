package com.cannontech.web.common.pao;

import org.springframework.ui.ModelMap;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.user.YukonUserContext;

public interface PaoPopupHelper {

    /**
     * Takes a device collection and adds the appropriate attributes to the
     * model to build a 'selected devices' popup.
     * 
     * NOTE:
     * After calling .buildPopupModel(..) you should use a JSP
     * based upon common/device/deviceListPopup.jsp, such as
     * deviceDataMonitor/deviceViolationsPopup.jsp
     */
    public void buildPopupModel(DeviceCollection collection, ModelMap model,
            YukonUserContext context);
}
