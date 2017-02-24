package com.cannontech.multispeak.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncService;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.user.YukonUserContext;

public class MspDeviceGroupSyncHandler {

    @Autowired @Qualifier("v3") private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncServiceV3;
    @Autowired @Qualifier("v5") private MultispeakDeviceGroupSyncService multispeakDeviceGroupSyncServiceV5;

    public void startDeviceGroupSync(MultispeakVendor mspVendor,
            MultispeakDeviceGroupSyncType multispeakDeviceGroupSyncType, YukonUserContext userContext) {

        MultispeakInterface cb_server_v3 =
            mspVendor.getMspInterfaceMap().get(MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

        if (cb_server_v3 != null) {
            multispeakDeviceGroupSyncServiceV3.startSyncForType(multispeakDeviceGroupSyncType, userContext);
        } else {
            MultispeakInterface cb_server_v5 =
                mspVendor.getMspInterfaceMap().get(MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V5));

            if (cb_server_v5 != null) {
                multispeakDeviceGroupSyncServiceV5.startSyncForType(multispeakDeviceGroupSyncType, userContext);
            }
        }
    }
}
