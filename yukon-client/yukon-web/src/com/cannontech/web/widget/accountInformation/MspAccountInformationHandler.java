package com.cannontech.web.widget.accountInformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.user.YukonUserContext;

public class MspAccountInformationHandler {
    @Autowired private MspAccountInformationV3 mspAccountInformationV3;
    @Autowired private MspAccountInformationV5 mspAccountInformationV5;

    public ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspPrimaryCISVendor, ModelAndView mav,
            YukonUserContext userContext) {

        if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR) != null) {
            if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR).getVersion().equals(
                MultiSpeakVersion.V3.getVersion())) {
                return mspAccountInformationV3.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
            } else {
                return mspAccountInformationV5.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
            }
        }
        return mav;
    }

}
