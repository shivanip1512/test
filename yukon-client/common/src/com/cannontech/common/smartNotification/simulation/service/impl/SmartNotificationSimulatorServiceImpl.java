package com.cannontech.common.smartNotification.simulation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.common.smartNotification.simulation.service.SmartNotificationSimulatorService;
import com.cannontech.infrastructure.simulation.service.*;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class SmartNotificationSimulatorServiceImpl implements SmartNotificationSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationSimulatorServiceImpl.class);
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired protected IDatabaseCache cache;
    @Autowired private SmartNotificationEventCreationService eventCreationService;
    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private InfrastructureWarningsGeneratorService infrastructureWarningsGeneratorService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private SmartNotificationSubscriptionService subscriptionService;
    @Autowired private YukonUserDao yukonUserDao;
    
    private Executor executor = Executors.newCachedThreadPool();
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setPubSubDomain(false);
    }
    
    @Override
    public SimulatorResponseBase clearAllSubscriptions() {
        subscriptionDao.deleteAllSubcriptions();
        return new SimulatorResponseBase(true);
    }
    
    @Override
    public SimulatorResponseBase clearAllEvents() {
        eventDao.deleteAllEvents();
        return new SimulatorResponseBase(true);
    }

    @Override
    public SimulatorResponseBase createEvents(int waitTime, int eventsPerMessage, int numberOfMessages) {
        Map<SmartNotificationEventType, List<SmartNotificationSubscription>> subscriptionsByType =
                subscriptionDao.getAllSubscriptions().stream().collect(Collectors.groupingBy(e -> e.getType()));
            
            // send total "numberOfMessages" with "eventsPerMessage" waiting "waitTime"
            // between sending each set of events
            
            ArrayList<Integer> deviceIds = new ArrayList<>(cache.getAllMeters().keySet());
            
            Set<SmartNotificationEventType> done = Collections.synchronizedSet(new HashSet<>());
            
            Random random = new Random();
            Arrays.asList(SmartNotificationEventType.values()).forEach(type -> {
                executor.execute(() -> {
                    log.info("Simulating events for " + type);
                    List<SmartNotificationSubscription> subscriptions = subscriptionsByType.get(type);
                    if (subscriptions != null) {
                        List<SmartNotificationEvent> events = new ArrayList<>();

                        int nextIndex = -1;
                        for (int i = 0; i < numberOfMessages; i++) {
                            int index = random.nextInt(deviceIds.size());
                            nextIndex = getNextSubscriptionIndex(subscriptions, nextIndex);
                            
                            if(type == SmartNotificationEventType.INFRASTRUCTURE_WARNING){
                                List<InfrastructureWarningType> types = Lists.newArrayList(InfrastructureWarningType.values());
                                Collections.shuffle(types);
                                InfrastructureWarning warning = infrastructureWarningsGeneratorService.generate(types.get(0));
                                events.add(InfrastructureWarningsEventAssembler.assemble(Instant.now(), warning));
                            } else if (type == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
                                int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(
                                    subscriptions.get(nextIndex).getParameters());
                                String monitorName = monitorCacheService.getDeviceDataMonitors().stream().filter(
                                    m -> m.getId() == monitorId).findFirst().get().getName();
                                events.add(DeviceDataMonitorEventAssembler.assemble(Instant.now(), monitorId, monitorName,
                                    MonitorState.IN_VIOLATION, deviceIds.get(index)));
                            }
                        }

                        log.info("Created events " + events.size() + " " + type);

                        for (List<SmartNotificationEvent> part : Lists.partition(events, eventsPerMessage)) {
                            try {
                                TimeUnit.SECONDS.sleep(waitTime);
                                log.info("Sending " + part.size() + " " + type);
                                eventCreationService.send(type, part);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    done.add(type);
                });

            });

            while (done.size() != subscriptionsByType.size()) {
                //wait to report success until all events are generated.
                continue;
            }
            
        return new SimulatorResponseBase(true);
    }
    
    @Override
    public SimulatorResponseBase saveSubscription(SmartNotificationSubscription subscription, int userGroupId,
                                                  boolean generateTestEmailAddresses, YukonUserContext userContext) {
        List<Integer> userIds = yukonUserDao.getUserIdsForUserGroup(userGroupId);
        userIds.forEach(id -> {
            subscription.setId(0);
            subscription.setUserId(id);
            if (generateTestEmailAddresses) {
                subscription.setRecipient(id + "@eaton");
            }
            subscriptionService.saveSubscription(subscription, userContext);
        });
        return new SimulatorResponseBase(true);
    }
    
    @Override 
    public SimulatorResponseBase startDailyDigest(int dailyDigestHour) {
        log.info("Initiating a test daily digest for " + dailyDigestHour + ":00");
        jmsTemplate.convertAndSend(JmsApiDirectory.SMART_NOTIFICATION_DAILY_DIGEST_TEST.getQueue().getName(), 
                                   new DailyDigestTestParams(dailyDigestHour));
        return new SimulatorResponseBase(true);
    }
    
    /**
     * Returns index of the next object if object doesn't exists, start from 0.
     */
    private int getNextSubscriptionIndex(List<SmartNotificationSubscription> subscriptions, int index) {
        try {
            subscriptions.get(index + 1);
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
        return index + 1;
    }
    
    public void saveSettings(SmartNotificationSimulatorSettings settings) {
        log.debug("Saving SmartNotificationSimlatorSettings to YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_DAILY_DIGEST_HOUR, settings.getDailyDigestHour());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_USER_GROUP_ID, settings.getUserGroupId());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_GENERATE_TEST_EMAIL, settings.isGenerateTestEmail());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE, settings.getEventsPerType());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE, settings.getEventsPerMessage());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_WAIT_TIME_SEC, settings.getWaitTimeSec());
    }
    
    public SmartNotificationSimulatorSettings getCurrentSettings() {
        log.debug("Getting SmartNotificationSimlatorSettings from db.");
        SmartNotificationSimulatorSettings settings = new SmartNotificationSimulatorSettings(
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_DAILY_DIGEST_HOUR),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_USER_GROUP_ID),
            yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_GENERATE_TEST_EMAIL),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_WAIT_TIME_SEC));
        return settings;
    }

}
