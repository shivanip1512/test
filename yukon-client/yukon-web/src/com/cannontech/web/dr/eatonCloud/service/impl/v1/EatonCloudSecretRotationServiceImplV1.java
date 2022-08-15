package com.cannontech.web.dr.eatonCloud.service.impl.v1;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
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
    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudSecretRotationServiceImplV1.class);
    private static final int minimumRotationInterval = 5;
    
    
    private Map<Integer, Instant> nextRotationTime = new HashMap<Integer, Instant>();
    private Map<Integer, GlobalSettingType> secretToGlobalSettings = Map.of(1, GlobalSettingType.EATON_CLOUD_SECRET, 2,
            GlobalSettingType.EATON_CLOUD_SECRET2);
    
    @PostConstruct
    public void init() {
        secretToGlobalSettings.keySet().stream().forEach(secretNumber -> nextRotationTime.put(secretNumber, new Instant())); // This allows the secrets to be immediately rotated upon service start
    }
    
    @Override
    public EatonCloudSecretExpiryTime getSecretExpiryTime() {
        EatonCloudServiceAccountDetailV1 detail = eatonCloudCommunicationService.getServiceAccountDetail();
        log.debug(new EatonCloudSecretExpiryTime(detail.getExpiryTime(1), detail.getExpiryTime(2)));
        return new EatonCloudSecretExpiryTime(detail.getExpiryTime(1), detail.getExpiryTime(2));
    }

    @Override
    public Instant rotateSecret(int secretNumber, LiteYukonUser user) {
        EatonCloudSecretValueV1 value = eatonCloudCommunicationService.rotateAccountSecret(secretNumber);
        settingDao.updateSetting(new GlobalSetting(secretToGlobalSettings.get(secretNumber), value.getSecret()), user);
        eatonCloudEventLogService.secretRotationSuccess("secret" + secretNumber, user, 1);
        if (value.getExpiryTime() != null) {
            nextRotationTime.put(secretNumber, new Instant().plus(Duration.standardMinutes(minimumRotationInterval)));
        }
        return value.getExpiryTime() == null ? null : new Instant(value.getExpiryTime());
    }

    @Override
    public Boolean secretRotationAllowed(int secretNumber) {
        return nextRotationTime.get(secretNumber).isBeforeNow();
    }

    @Override
    public Instant timeNextRotationAllowed(int secretNumber) {
        return nextRotationTime.get(secretNumber);
    }
}
