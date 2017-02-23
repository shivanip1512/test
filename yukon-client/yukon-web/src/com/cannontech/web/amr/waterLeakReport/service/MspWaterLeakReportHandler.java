package com.cannontech.web.amr.waterLeakReport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.user.YukonUserContext;

public class MspWaterLeakReportHandler {

    @Autowired private MspWaterLeakReportV3 mspWaterLeakReportV3;
    @Autowired private MspWaterLeakReportV5 mspWaterLeakReportV5;

    public String getCisDetails(ModelMap model, YukonUserContext userContext, int paoId,
            MultispeakVendor mspPrimaryCISVendor) {

        MultispeakInterface cb_server_v3 =
            mspPrimaryCISVendor.getMspInterfaceMap().get(
                MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

        if (cb_server_v3 != null) {
            return mspWaterLeakReportV3.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
        } else {
            MultispeakInterface cb_server_v5 =
                mspPrimaryCISVendor.getMspInterfaceMap().get(
                    MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V5));

            if (cb_server_v5 != null) {
                return mspWaterLeakReportV5.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
            }
        }
        return "";
    }
}
