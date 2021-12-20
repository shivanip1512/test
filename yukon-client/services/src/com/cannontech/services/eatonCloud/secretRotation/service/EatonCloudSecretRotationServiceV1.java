package com.cannontech.services.eatonCloud.secretRotation.service;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.SimpleAlert;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSecretValueV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudServiceAccountDetailV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.services.eatonCloud.authToken.service.EatonCloudAuthTokenServiceV1;
import com.cannontech.simulators.message.request.EatonCloudSecretRotationSimulationRequest;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;

public class EatonCloudSecretRotationServiceV1 {

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private GlobalSettingUpdateDao settingUpdateDao;
    @Autowired private EatonCloudAuthTokenServiceV1 eatonCloudAuthTokenServiceV1;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudSecretRotationServiceV1.class);
    
    private Map<GlobalSettingType, Integer> globalSettingsToSecret = Map.of(GlobalSettingType.EATON_CLOUD_SECRET, 1,
            GlobalSettingType.EATON_CLOUD_SECRET2, 2);
    
    private Map<GlobalSettingType, AtomicInteger> secretValidations = new ConcurrentHashMap<GlobalSettingType, AtomicInteger>();
    private Map<GlobalSettingType, AtomicInteger> secretRotations = new ConcurrentHashMap<GlobalSettingType, AtomicInteger>();
    
    private final int numberOfTimesToRetry = 3;
    private int retryIntervalMinutes = 10;
    
    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NEW_ALERT_CREATION);
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        if (Strings.isNullOrEmpty(serviceAccountId)) {
            return;
        }
        executor.scheduleAtFixedRate(() -> {
            rotateSecrets();
        }, 5, 1, TimeUnit.DAYS);
    }

    public void startSimulation(EatonCloudSecretRotationSimulationRequest request) {
        // we are going wait 1 minute instead of 10 for each retry to make the testing easier
        retryIntervalMinutes = 1;
        log.info("startSimulation");
        rotateSecrets();
    }

    private void rotateSecrets() {
        try {
            EatonCloudServiceAccountDetailV1 detail = eatonCloudCommunicationService.getServiceAccountDetail();
            globalSettingsToSecret.keySet().stream().sorted().forEach(type -> rotateAndValidateSecret(detail, type));
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error("Account info retrieval failed:{}",
                    new GsonBuilder().setPrettyPrinting().create().toJson(e.getErrorMessage()));
            //validating existing secrets
            globalSettingsToSecret.keySet().forEach(type -> validateSecret(type));
        }
    }

    /**
     * If secret expires within 6 month get a new secret otherwise check is the secret is still valid by obtaining the token 
     */
    private void rotateAndValidateSecret(EatonCloudServiceAccountDetailV1 detail, GlobalSettingType type) {
        Instant secretExpiryTime = getExpiryTime(globalSettingsToSecret.get(type), detail);
        if (DateTime.now().plusMonths(6).isAfter(secretExpiryTime)) {
            rotateSecret(type, secretExpiryTime);
        } else {
            validateSecret(type);
        }
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
    
    /**
     * Obtains a token to validate the secret, if error is received, tries again ever 10 minutes up to 3 times.
     */
    private void validateSecret(GlobalSettingType type) {
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        AtomicInteger currentTry = secretValidations.getOrDefault(type, new AtomicInteger(1));
        String secret = "secret" + globalSettingsToSecret.get(type);
        try {
            eatonCloudAuthTokenServiceV1.retrieveNewToken(type, serviceAccountId);
            secretValidations.remove(type);
            log.info("({} of {}) {} token retrieval successful.", currentTry.get(), numberOfTimesToRetry, secret);
        } catch (EatonCloudCommunicationExceptionV1 e) {
            if (currentTry.get() == numberOfTimesToRetry) {
                log.error("({} of {}) {} token retrieval failed: {}", currentTry.get(), numberOfTimesToRetry, secret,
                        e.getMessage());
                secretValidations.remove(type);
                eatonCloudEventLogService.tokenRetrievalFailed(secret, e.getMessage(), currentTry.get());
                createAlert(AlertType.RFN_DEVICE_CREATION_FAILED, secret, null);
            } else {
                log.error("({} of {}) {} token retrieval failed: {} Next try in {} minutes.", currentTry.get(),
                        numberOfTimesToRetry, secret,
                        e.getMessage(), retryIntervalMinutes);
                executor.schedule(() -> validateSecret(type), retryIntervalMinutes, TimeUnit.MINUTES);
                currentTry.incrementAndGet();
                secretValidations.put(type, currentTry);
            }
        }
    }

    /**
     * Attempts to obtain a new secret from Eaton Cloud if error is received, tries again every 10 minutes up to 3 times.
     * After getting new secret or failing on the 3rd attempt, validates existing secret in case of failure, or new secret if
     * succeeded.
     */
    private void rotateSecret(GlobalSettingType type, Instant secretExpiryTime) {
        AtomicInteger currentTry = secretRotations.getOrDefault(type, new AtomicInteger(1));
        String secret = "secret" + globalSettingsToSecret.get(type);
        try {
            EatonCloudSecretValueV1 value = eatonCloudCommunicationService.rotateAccountSecret(globalSettingsToSecret.get(type));
            settingUpdateDao.updateSetting(new GlobalSetting(type, value.getSecret()), YukonUserContext.system.getYukonUser());
            secretRotations.remove(type);
            log.info("({} of {}) {} rotation is successful.", currentTry.get(), numberOfTimesToRetry, secret);
            eatonCloudEventLogService.secretRotationSuccess(secret, currentTry.get());
            validateSecret(type);
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error("({} of {}) {} rotation failed:{}", secret, globalSettingsToSecret.get(type), e.getErrorMessage());
            if (currentTry.get() == numberOfTimesToRetry) {
                secretRotations.remove(type);
                eatonCloudEventLogService.secretRotationFailed(secret, e.getMessage(), currentTry.get());
                createAlert(AlertType.EATON_CLOUD_CREDENTIAL_UPDATE_FAILURE, secret, secretExpiryTime.toDate());
                validateSecret(type);
            } else {
                executor.schedule(() -> rotateSecret(type, secretExpiryTime), retryIntervalMinutes, TimeUnit.MINUTES);
                currentTry.incrementAndGet();
                secretRotations.put(type, currentTry);
            }
        }
    }
    
    /**
     * Creates alert and sends it to WS for processing
     */
    private void createAlert(AlertType type, String secret, Date expireTime) {
        ResolvableTemplate template = new ResolvableTemplate("yukon.common.alerts." + type);
        String url = "TBD";
        template.addData("url", url);
        template.addData("secret", secret);
        if (expireTime != null) {
            DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.FULL, YukonUserContext.system);
            template.addData("expireTime", dateFormatter.format(expireTime));
        }
  
        SimpleAlert alert = new SimpleAlert(type, new Date(), template);
        jmsTemplate.convertAndSend(alert);
    }
}
