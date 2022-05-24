package com.cannontech.web.multispeak;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.MultispeakSyncType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.multispeak.service.impl.v5.MultispeakEnrollmentSyncService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.waterLeakReport.service.MspWaterLeakReportV3;
import com.cannontech.web.amr.waterLeakReport.service.MspWaterLeakReportV4;
import com.cannontech.web.amr.waterLeakReport.service.MspWaterLeakReportV5;
import com.cannontech.web.widget.accountInformation.MspAccountInformationV3;
import com.cannontech.web.widget.accountInformation.MspAccountInformationV4;
import com.cannontech.web.widget.accountInformation.MspAccountInformationV5;

/**
 * Identifies MSP version and continues execution based on MSP version.
 */
public class MspHandler {

    @Autowired private MspAccountInformationV3 mspAccountInformationV3;
    @Autowired private MspAccountInformationV4 mspAccountInformationV4;
    @Autowired private MspAccountInformationV5 mspAccountInformationV5;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private com.cannontech.multispeak.dao.v5.MspObjectDao mspObjectDaoV5;
    @Autowired private com.cannontech.multispeak.dao.v4.MspObjectDao mspObjectDaoV4;
    @Autowired private MspWaterLeakReportV3 mspWaterLeakReportV3;
    @Autowired private MspWaterLeakReportV4 mspWaterLeakReportV4;
    @Autowired private MspWaterLeakReportV5 mspWaterLeakReportV5;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakDao multispeakDao;
    @Autowired @Qualifier("v3") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV3;
    @Autowired @Qualifier("v4") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV4;
    @Autowired @Qualifier("v5") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV5;
    @Autowired private  MultispeakEnrollmentSyncService multispeakEnrollmentSyncService;

    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE = "GetDomainMembers - substationCode";
    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE_V4 = "GetDomainsByDomainNames - substationCode";
    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE_V5 = "GetDomainsByDomainNames - substationCode";

    public void startDeviceGroupSync(MultispeakSyncType type,
            YukonUserContext userContext) {
        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);
            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    multispeakDeviceGroupSyncServiceV3.startSyncForType(type, userContext);
                } 
                else if (cisVersion == MultiSpeakVersion.V4) {
                    multispeakDeviceGroupSyncServiceV4.startSyncForType(type, userContext);
                }else if (cisVersion == MultiSpeakVersion.V5) {
                    multispeakDeviceGroupSyncServiceV5.startSyncForType(type, userContext);
                }
            }
        }
    }

    public MultispeakDeviceGroupSyncServiceBase getDeviceGroupSyncService() {
        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);
            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    return multispeakDeviceGroupSyncServiceV3;
                }
                else if (cisVersion == MultiSpeakVersion.V4) {
                    return multispeakDeviceGroupSyncServiceV4;
                } else if (cisVersion == MultiSpeakVersion.V5) {
                    return multispeakDeviceGroupSyncServiceV5;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<String> getMspSubstationName() {
        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);
            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    return mspObjectDao.getMspSubstationName(mspPrimaryCISVendor);

                } else if (cisVersion == MultiSpeakVersion.V4) {
                    return mspObjectDaoV4.getMspSubstationName(mspPrimaryCISVendor);
                }
                else if (cisVersion == MultiSpeakVersion.V5) {
                    return mspObjectDaoV5.getMspSubstationName(mspPrimaryCISVendor);
                }
            }
        }
        return Collections.emptyList();
    }

    public void invalidSubstationName(String mspSubstationName) {
        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);

            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE, mspSubstationName,
                        mspPrimaryCISVendor.getCompanyName());

                } else if (cisVersion == MultiSpeakVersion.V4) {
                    multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE_V4,
                            mspSubstationName, mspPrimaryCISVendor.getCompanyName());
                }
                else if (cisVersion == MultiSpeakVersion.V5) {
                    multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE_V5,
                            mspSubstationName, mspPrimaryCISVendor.getCompanyName());
                }
            }
        }
    }

    public String getCisDetails(ModelMap model, YukonUserContext userContext, int paoId) {

        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);
            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    return mspWaterLeakReportV3.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
                } else if (cisVersion == MultiSpeakVersion.V4) {
                    return mspWaterLeakReportV4.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
                } else if (cisVersion == MultiSpeakVersion.V5) {
                    return mspWaterLeakReportV5.getCisDetails(model, userContext, paoId, mspPrimaryCISVendor);
                }
            }
        }
        return "";
    }

    public ModelAndView getMspInformation(YukonMeter meter, ModelAndView mav, YukonUserContext userContext) {

        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);
            if (cisVersion != null) {
                if (cisVersion == MultiSpeakVersion.V3) {
                    return mspAccountInformationV3.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
                } else if (cisVersion == MultiSpeakVersion.V4) {
                    return mspAccountInformationV4.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
                } else if (cisVersion == MultiSpeakVersion.V5) {
                    return mspAccountInformationV5.getMspInformation(meter, mspPrimaryCISVendor, mav, userContext);
                }
            }
        }
        return mav;
    }

    public MultiSpeakVersion getMSPVersion() {
        MultispeakVendor mspPrimaryCISVendor = getMspPrimaryCISVendor();
        if (mspPrimaryCISVendor != null) {
            return multispeakFuncs.getPrimaryCISVersion(mspPrimaryCISVendor);
        }
        return null;
    }

    public void startEnrollmentSync() {
        multispeakEnrollmentSyncService.startEnrollmentSync();
    }

    public MultispeakEnrollmentSyncService getMultispeakEnrollmentSyncService() {
        return multispeakEnrollmentSyncService;
    }

    /**
     * Returns the MultiSpeak Vendor that represents the Primary CIS vendor.
     * If no vendor is defined, then return null
     */
    private MultispeakVendor getMspPrimaryCISVendor() {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        if (vendorId <= 0) {
            return null;
        }
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
        return mspVendor;
    }
    
    /**
     * Checks whether synchronization for enrollment is supported or not.
     */
    public boolean isEnrollmentSyncSupported() {
        List<MultispeakVendor> allVendors = multispeakDao.getMultispeakVendors(true);
        boolean isEnrollmentSyncSupportExists = allVendors.stream()
                                                          .anyMatch(multispeakVendor -> multispeakVendor.getMspInterfaces()
                                                                                                        .stream()
                                                                                                        .anyMatch(mspInterface -> mspInterface.getMspInterface()
                                                                                                                                              .equals(MultispeakDefines.NOT_Server_DR_STR)));
        return isEnrollmentSyncSupportExists;
    }
}
