package com.cannontech.web.amr.waterLeakReport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.user.YukonUserContext;

public class MspWaterLeakReportHandler {

    @Autowired private MspWaterLeakReportV3 mspWaterLeakReportV3;
    @Autowired private MspWaterLeakReportV5 mspWaterLeakReportV5;

    public String getCisDetails(ModelMap model, YukonUserContext userContext, int paoId,
            MultispeakVendor mspPrimaryCISVendor) {

        if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR) != null) {
            if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR).getVersion().equals(
                MultiSpeakVersion.V3.getVersion())) {
                return mspWaterLeakReportV3.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
            } else {
                return mspWaterLeakReportV5.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
            }

        }
        return "";
    }
}
