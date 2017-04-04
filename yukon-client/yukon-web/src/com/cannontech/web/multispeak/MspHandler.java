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
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncType;
import com.cannontech.multispeak.service.impl.MultispeakDeviceGroupSyncServiceBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.meter.service.impl.MspSearchFieldsProviderV3;
import com.cannontech.web.amr.meter.service.impl.MspSearchFieldsProviderV5;
import com.cannontech.web.amr.waterLeakReport.service.MspWaterLeakReportV3;
import com.cannontech.web.amr.waterLeakReport.service.MspWaterLeakReportV5;
import com.cannontech.web.widget.accountInformation.MspAccountInformationV3;
import com.cannontech.web.widget.accountInformation.MspAccountInformationV5;

public class MspHandler {

    @Autowired private MspAccountInformationV3 mspAccountInformationV3;
    @Autowired private MspAccountInformationV5 mspAccountInformationV5;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private com.cannontech.multispeak.dao.v5.MspObjectDao mspObjectDaoV5;
    @Autowired private MspSearchFieldsProviderV3 fieldsProviderV3;
    @Autowired private MspSearchFieldsProviderV5 fieldsProviderV5;
    @Autowired private MspWaterLeakReportV3 mspWaterLeakReportV3;
    @Autowired private MspWaterLeakReportV5 mspWaterLeakReportV5;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired @Qualifier("v3") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV3;
    @Autowired @Qualifier("v5") private MultispeakDeviceGroupSyncServiceBase multispeakDeviceGroupSyncServiceV5;

    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE = "GetDomainMembers - substationCode";
    private static final String DOMAIN_MEMBERS_SUBSTATION_CODE_V5 = "GetDomainsByDomainNames - substationCode";

    public void startDeviceGroupSync(MultispeakVendor mspVendor,
            MultispeakDeviceGroupSyncType multispeakDeviceGroupSyncType, YukonUserContext userContext) {

        MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);
        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                multispeakDeviceGroupSyncServiceV3.startSyncForType(multispeakDeviceGroupSyncType, userContext);

            } else if (cisVersion == MultiSpeakVersion.V5) {
                multispeakDeviceGroupSyncServiceV5.startSyncForType(multispeakDeviceGroupSyncType, userContext);
            }
        }
    }

    public MultispeakDeviceGroupSyncServiceBase getDeviceGroupSyncService(MultispeakVendor mspVendor) {

        MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);
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

    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {

        MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);
        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                return mspObjectDao.getMspSubstationName(mspVendor);

            } else if (cisVersion == MultiSpeakVersion.V5) {
                return mspObjectDaoV5.getMspSubstationName(mspVendor);
            }
        }
        return Collections.emptyList();
    }

    public void invalidSubstationName(MultispeakVendor mspVendor, String mspSubstationName) {
        MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);

        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE, mspSubstationName,
                    mspVendor.getCompanyName());

            } else if (cisVersion == MultiSpeakVersion.V5) {
                multispeakEventLogService.invalidSubstationName(DOMAIN_MEMBERS_SUBSTATION_CODE_V5, mspSubstationName,
                    mspVendor.getCompanyName());
            }
        }
    }

    public String getCisDetails(ModelMap model, YukonUserContext userContext, int paoId,
            MultispeakVendor mspVendor) {
        MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);
        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                return mspWaterLeakReportV3.getCisDetails(model, userContext, paoId, mspVendor);

            } else if (cisVersion == MultiSpeakVersion.V5) {
                return mspWaterLeakReportV5.getCisDetails(model, userContext, paoId, mspVendor);
            }
        }
        return "";
    }

    public ModelAndView getMspInformation(YukonMeter meter, MultispeakVendor mspVendor, ModelAndView mav,
            YukonUserContext userContext) {

        MultiSpeakVersion cisVersion = multispeakFuncs.getPrimaryCISVersion(mspVendor);
        if (cisVersion != null) {
            if (cisVersion == MultiSpeakVersion.V3) {
                return mspAccountInformationV3.getMspInformation(meter, mspVendor, mav, userContext);

            } else if (cisVersion == MultiSpeakVersion.V5) {
                return mspAccountInformationV5.getMspInformation(meter, mspVendor, mav, userContext);
            }
        }
        return mav;
    }
}
