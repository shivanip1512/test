package com.cannontech.web.admin.substations.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;

public class MspObjectDaoHandler {
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private com.cannontech.multispeak.dao.v5.MspObjectDao mspObjectDaoV5;

    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE = "getDomainMembers - substationCode";
    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE_V5 = "getDomainsByDomainNames - substationCode";

    public List<String> getMspSubstationName(MultispeakVendor mspPrimaryCISVendor) {
        if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR) != null) {
            if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR).getVersion().equals(
               MultiSpeakVersion.V3.getVersion())) {
                return mspObjectDao.getMspSubstationName(mspPrimaryCISVendor);
            } else {
                return mspObjectDaoV5.getMspSubstationName(mspPrimaryCISVendor);
            }
        }
        return Collections.emptyList();
    }

    public void invalidSubstationName(MultispeakVendor mspPrimaryCISVendor, String mspSubstationName) {
        if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR) != null) {
            if (mspPrimaryCISVendor.getMspInterfaceMap().get(MultispeakDefines.CB_Server_STR).getVersion().equals(
                MultiSpeakVersion.V3.getVersion())) {
                multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE, mspSubstationName,
                    mspPrimaryCISVendor.getCompanyName());
            } else {
                multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE_V5, mspSubstationName,
                    mspPrimaryCISVendor.getCompanyName());
            }
        }
    }
}