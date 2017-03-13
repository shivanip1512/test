package com.cannontech.multispeak.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.user.YukonUserContext;

public class MspDeviceGroupSyncHandler {

    @Autowired @Qualifier("v3") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV3;
    @Autowired @Qualifier("v5") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV5;

    public void startDeviceGroupSync(MultispeakVendor mspVendor,
            MultispeakDeviceGroupSyncType multispeakDeviceGroupSyncType, YukonUserContext userContext) {

        MultiSpeakVersion cisVersion = getPrimaryCISVersion(mspVendor);
        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                multispeakDeviceGroupSyncServiceV3.startSyncForType(multispeakDeviceGroupSyncType, userContext);

            } else if (cisVersion == MultiSpeakVersion.V5) {
                multispeakDeviceGroupSyncServiceV5.startSyncForType(multispeakDeviceGroupSyncType, userContext);
            }
        }
    }

    public MultispeakDeviceGroupSyncServiceBase getDeviceGroupSyncService(MultispeakVendor mspVendor) {

        MultiSpeakVersion cisVersion = getPrimaryCISVersion(mspVendor);
        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                return multispeakDeviceGroupSyncServiceV3;

            } else if (cisVersion == MultiSpeakVersion.V5) {
                return multispeakDeviceGroupSyncServiceV5;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    
    private static MultiSpeakVersion getPrimaryCISVersion(MultispeakVendor mspVendor) {
        MultispeakInterface cb_server_v3 =
            mspVendor.getMspInterfaceMap().get(
                MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

        if (cb_server_v3 != null) {
            return cb_server_v3.getVersion();
        } else {
            MultispeakInterface cb_server_v5 =
                mspVendor.getMspInterfaceMap().get(
                    MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V5));

            if (cb_server_v5 != null) {
                return cb_server_v5.getVersion();
            }
        }
        return null;
    }
}
