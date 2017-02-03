package com.cannontech.web.widget.accountInformation;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.user.YukonUserContext;

public interface MspAccountInformation {

    /**
     * Make multispeak calls to get information to display.
     * 
     * @param meter - Meter for which information has to be retrieved.
     * @param mspVendor - Multispeak CB client
     * @param mav - Model object to update
     * @param userContext - User context.
     */
    ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspVendor, ModelAndView mav,
            YukonUserContext userContext);
}
