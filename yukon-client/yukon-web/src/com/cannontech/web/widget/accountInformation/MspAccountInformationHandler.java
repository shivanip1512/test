package com.cannontech.web.widget.accountInformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.user.YukonUserContext;

public class MspAccountInformationHandler {
    @Autowired private MspAccountInformationV3 mspAccountInformationV3;
    @Autowired private MspAccountInformationV5 mspAccountInformationV5;

    public ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspPrimaryCISVendor, ModelAndView mav,
            YukonUserContext userContext) {

        MultispeakInterface cb_server_v3 =
            mspPrimaryCISVendor.getMspInterfaceMap().get(
                MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

        if (cb_server_v3 != null) {
            return mspAccountInformationV3.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
        } else {
            MultispeakInterface cb_server_v5 =
                mspPrimaryCISVendor.getMspInterfaceMap().get(
                    MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V5));

            if (cb_server_v5 != null) {
                return mspAccountInformationV5.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
            }
        }
        return mav;
    }

}
