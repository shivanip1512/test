package com.cannontech.web.dr.eatonCloud.service.impl.v1;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSecretValueV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudServiceAccountDetailV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.web.dr.eatonCloud.model.EatonCloudSecretExpiryTime;
import com.cannontech.web.dr.eatonCloud.service.v1.EatonCloudSecretRotationServiceV1;

public class EatonCloudSecretRotationServiceImplV1 implements EatonCloudSecretRotationServiceV1 {

    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private GlobalSettingUpdateDao settingDao;
    private static final Logger log = YukonLogManager.getLogger(EatonCloudSecretRotationServiceImplV1.class);
    
    private Map<Integer, GlobalSettingType> secretToGlobalSettings = Map.of(1, GlobalSettingType.EATON_CLOUD_SECRET, 2,
            GlobalSettingType.EATON_CLOUD_SECRET2);

    @Override
    public EatonCloudSecretExpiryTime getSecretExpiryTime() {
        EatonCloudServiceAccountDetailV1 detail = eatonCloudCommunicationService.getServiceAccountDetail();
        log.debug(new EatonCloudSecretExpiryTime(getExpiryTime(1, detail), getExpiryTime(2, detail)));
        return new EatonCloudSecretExpiryTime(getExpiryTime(1, detail), getExpiryTime(2, detail));
    }

    @Override
    public Instant rotateSecret(int secretNumber, LiteYukonUser user) {
        EatonCloudSecretValueV1 value = eatonCloudCommunicationService.rotateAccountSecret(secretNumber);
        settingDao.updateSetting(new GlobalSetting(secretToGlobalSettings.get(secretNumber), value.getSecret()), user);
        return value.getExpiryTime() == null ? null : new Instant(value.getExpiryTime());
    }

    private Instant getExpiryTime(int secretNumber, EatonCloudServiceAccountDetailV1 detail) {
        Date time1 = detail.getSecrets().stream()
                .filter(s -> s.getName().equals("secret" + secretNumber))
                .findFirst()
                .orElse(null)
                .getExpiryTime();
        if (time1 == null) {
            return null;
        }
        return new Instant(time1);
    }
}
