package com.cannontech.web.admin.substations.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.MultispeakInterface;

public class MspObjectDaoHandler {
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private com.cannontech.multispeak.dao.v5.MspObjectDao mspObjectDaoV5;

    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE = "GetDomainMembers - substationCode";
    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE_V5 = "GetDomainsByDomainNames - substationCode";

    public List<String> getMspSubstationName(MultispeakVendor mspPrimaryCISVendor) {

        MultispeakInterface cb_server_v3 =
            mspPrimaryCISVendor.getMspInterfaceMap().get(
                MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

        if (cb_server_v3 != null) {
            return mspObjectDao.getMspSubstationName(mspPrimaryCISVendor);
        } else {
            MultispeakInterface cb_server_v5 =
                mspPrimaryCISVendor.getMspInterfaceMap().get(
                    MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V5));

            if (cb_server_v5 != null) {
                return mspObjectDaoV5.getMspSubstationName(mspPrimaryCISVendor);
            }
        }
        return Collections.emptyList();
    }

    public void invalidSubstationName(MultispeakVendor mspPrimaryCISVendor, String mspSubstationName) {

        MultispeakInterface cb_server_v3 =
            mspPrimaryCISVendor.getMspInterfaceMap().get(
                MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V3));

        if (cb_server_v3 != null) {
            multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE, mspSubstationName,
                mspPrimaryCISVendor.getCompanyName());
        } else {
            MultispeakInterface cb_server_v5 =
                mspPrimaryCISVendor.getMspInterfaceMap().get(
                    MultispeakVendor.buildMapKey(MultispeakDefines.CB_Server_STR, MultiSpeakVersion.V5));

            if (cb_server_v5 != null) {
                multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE_V5, mspSubstationName,
                    mspPrimaryCISVendor.getCompanyName());
            }
        }
    }
}