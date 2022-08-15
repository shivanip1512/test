package com.cannontech.services.eatonCloud.secretRotation.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
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
import com.cannontech.simulators.message.request.EatonCloudSecretRotationSimulationRequest;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Strings;

public class EatonCloudSecretRotationServiceV1 {

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private GlobalSettingUpdateDao settingUpdateDao;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private ConfigurationSource configSource;
    private YukonJmsTemplate jmsTemplate;
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudSecretRotationServiceV1.class);
    
    private Map<GlobalSettingType, Integer> globalSettingsToSecret = Map.of(GlobalSettingType.EATON_CLOUD_SECRET, 1,
            GlobalSettingType.EATON_CLOUD_SECRET2, 2);
    
    private List<GlobalSettingType> debugForceRotation = new ArrayList<>();
    
    private Map<GlobalSettingType, AtomicInteger> secretValidations = new ConcurrentHashMap<GlobalSettingType, AtomicInteger>();
    private Map<GlobalSettingType, AtomicInteger> secretRotations = new ConcurrentHashMap<GlobalSettingType, AtomicInteger>();
    
    private final int numberOfTimesToRetry = 3;
    private int retryIntervalMinutes = 10;
    
    @PostConstruct
    public void init() {
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        if (Strings.isNullOrEmpty(serviceAccountId)) {
            return;
        }
        
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NEW_ALERT_CREATION);
        executor.schedule(() -> {
            log.info("Scheduling Eaton Cloud Secret rotation and validation");
            executor.scheduleAtFixedRate(() -> {
                rotateSecrets();
            }, 0, 1, TimeUnit.DAYS);
        }, 15, TimeUnit.MINUTES);
        initDebugOptions();
    }

    /**
     * master.cfg DEV_FORCE_SECRET_ROTATION
     * secret1 - rotates secret1
     * secret2 - rotates secret2
     * secret1secret2 - rotates both secrets
     */
    private void initDebugOptions() {
        Optional<String> secrets = configSource.getOptionalString(MasterConfigString.DEV_FORCE_SECRET_ROTATION);
        if (secrets.isPresent()) {
            if (secrets.get().contains("secret1")) {
                debugForceRotation.add(GlobalSettingType.EATON_CLOUD_SECRET);
            }
            if (secrets.get().contains("secret2")) {
                debugForceRotation.add(GlobalSettingType.EATON_CLOUD_SECRET2);
            }
        }
    }

    public void startSimulation(EatonCloudSecretRotationSimulationRequest request) {
        // we are going wait 1 minute instead of 10 for each retry to make the testing easier
        secretValidations.clear();
        secretRotations.clear();
        retryIntervalMinutes = 1;
        log.info("Start Simulation");
        rotateSecrets();
    }

    private void rotateSecrets() {
        log.info("Running Eaton Cloud Secret rotation and validation");
        try {
            EatonCloudServiceAccountDetailV1 detail = eatonCloudCommunicationService.getServiceAccountDetail();
            globalSettingsToSecret.keySet().stream().sorted().forEach(type -> rotateAndValidateSecret(detail, type));
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error("Account info retrieval failed", e);
            //validating existing secrets
            globalSettingsToSecret.keySet().forEach(type -> validateSecret(type));
        }
    }

    /**
     * If secret expires within 6 month get a new secret otherwise check is the secret is still valid by obtaining the token 
     */
    private void rotateAndValidateSecret(EatonCloudServiceAccountDetailV1 detail, GlobalSettingType type) {
        Instant secretExpiryTime =  detail.getExpiryTime(globalSettingsToSecret.get(type));
        if (DateTime.now().plusMonths(6).isAfter(secretExpiryTime) || debugForceRotation.contains(type)) {
            rotateSecret(type, secretExpiryTime);
        } else {
            validateSecret(type);
        }
    }
    
    /**
     * Obtains a token to validate the secret, if error is received, tries again ever 10 minutes up to 3 times.
     */
    private void validateSecret(GlobalSettingType type) {
        synchronized (this) {
            String secret = "secret" + globalSettingsToSecret.get(type);
            AtomicInteger currentTry = secretValidations.getOrDefault(type, new AtomicInteger(1));
            try {
                eatonCloudCommunicationService.retrieveNewToken(type);
                secretValidations.remove(type);
                log.info("({} of {}) {} token retrieval successful.", currentTry.get(), numberOfTimesToRetry, secret);
            } catch (EatonCloudCommunicationExceptionV1 e) {
                if (currentTry.get() == numberOfTimesToRetry) {
                    log.error("({} of {}) {} token retrieval failed. Alert created:{}", currentTry.get(), numberOfTimesToRetry,
                            secret, AlertType.EATON_CLOUD_CREDENTIAL_INVALID, e);
                    secretValidations.remove(type);
                    eatonCloudEventLogService.tokenRetrievalFailed(secret, e.getDisplayMessage(), currentTry.get());
                    createAlert(AlertType.EATON_CLOUD_CREDENTIAL_INVALID, secret, null);
                } else {
                    log.error("({} of {}) {} token retrieval failed. Next try in {} minutes.", currentTry.get(),
                            numberOfTimesToRetry, secret, retryIntervalMinutes, e);
                    executor.schedule(() -> validateSecret(type), retryIntervalMinutes, TimeUnit.MINUTES);
                    currentTry.incrementAndGet();
                    secretValidations.put(type, currentTry);
                }
            }
        }
    }

    /**
     * Attempts to obtain a new secret from Eaton Cloud if error is received, tries again every 10 minutes up to 3 times.
     * After getting new secret or failing on the 3rd attempt, validates existing secret in case of failure, or new secret if
     * succeeded.
     */
    private void rotateSecret(GlobalSettingType type, Instant secretExpiryTime) {
        synchronized (this) {
            AtomicInteger currentTry = secretRotations.getOrDefault(type, new AtomicInteger(1));
            String secret = "secret" + globalSettingsToSecret.get(type);
            try {
                EatonCloudSecretValueV1 value = eatonCloudCommunicationService
                        .rotateAccountSecret(globalSettingsToSecret.get(type));
                settingUpdateDao.updateSetting(new GlobalSetting(type, value.getSecret()),
                        YukonUserContext.system.getYukonUser());
                secretRotations.remove(type);
                log.info("({} of {}) {} rotation is successful.", currentTry.get(), numberOfTimesToRetry, secret);
                eatonCloudEventLogService.secretRotationSuccess(secret, YukonUserContext.system.getYukonUser(), currentTry.get());
            } catch (EatonCloudCommunicationExceptionV1 e) {
                if (currentTry.get() == numberOfTimesToRetry) {
                    log.error("({} of {}) {} rotation failed. Alert created:{}", currentTry.get(), numberOfTimesToRetry, secret,
                            AlertType.EATON_CLOUD_CREDENTIAL_UPDATE_FAILURE, e);
                    secretRotations.remove(type);
                    eatonCloudEventLogService.secretRotationFailed(secret, YukonUserContext.system.getYukonUser(), e.getDisplayMessage(),
                            currentTry.get());
                    createAlert(AlertType.EATON_CLOUD_CREDENTIAL_UPDATE_FAILURE, secret, secretExpiryTime.toDate());
                    validateSecret(type);
                } else {
                    log.error("({} of {}) {} secret rotation failed. Next try in {} minutes.", currentTry.get(),
                            numberOfTimesToRetry, secret, retryIntervalMinutes, e);
                    executor.schedule(() -> rotateSecret(type, secretExpiryTime), retryIntervalMinutes, TimeUnit.MINUTES);
                    currentTry.incrementAndGet();
                    secretRotations.put(type, currentTry);
                }
            }
        }
    }
    
    /**
     * Creates alert and sends it to WS for processing
     */
    private void createAlert(AlertType type, String secret, Date expireTime) {
        ResolvableTemplate template = new ResolvableTemplate("yukon.common.alerts." + type);
        template.addData("secret", secret);
        if (expireTime != null) {
            DateFormat dateFormatter = dateFormattingService.getDateFormatter(DateFormattingService.DateFormatEnum.FULL, YukonUserContext.system);
            template.addData("expireTime", dateFormatter.format(expireTime));
        }
  
        SimpleAlert alert = new SimpleAlert(type, new Date(), template);
        jmsTemplate.convertAndSend(alert);
    }
}
