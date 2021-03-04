package com.cannontech.common.smartNotification.simulation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.DailyDigestTestParams;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler.MonitorState;
import com.cannontech.common.smartNotification.model.InfrastructureWarningsEventAssembler;
import com.cannontech.common.smartNotification.model.MeterDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;
import com.cannontech.common.smartNotification.simulation.service.SmartNotificationSimulatorService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.infrastructure.simulation.service.InfrastructureWarningsGeneratorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class SmartNotificationSimulatorServiceImpl implements SmartNotificationSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationSimulatorServiceImpl.class);
    @Autowired protected IDatabaseCache cache;
    @Autowired private SmartNotificationEventCreationService eventCreationService;
    @Autowired private SmartNotificationEventDao eventDao;
    @Autowired private InfrastructureWarningsGeneratorService infrastructureWarningsGeneratorService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private SmartNotificationSubscriptionService subscriptionService;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private DeviceGroupService deviceGroupService;

    private YukonJmsTemplate jmsTemplate;
    private static final Random rand = new Random();
    
    private Executor executor = Executors.newCachedThreadPool();

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SMART_NOTIFICATION_DAILY_DIGEST_TEST);
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
        
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE, numberOfMessages);
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE, eventsPerMessage);
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_WAIT_TIME_SEC, waitTime);
        
        Map<SmartNotificationEventType, List<SmartNotificationSubscription>> subscriptionsByType =
            subscriptionDao.getAllSubscriptions().stream().collect(Collectors.groupingBy(e -> e.getType()));

        log.info("Simulating events for types:", subscriptionsByType.keySet());
        Arrays.stream(SmartNotificationEventType.values()).forEach(type -> {
            executor.execute(() -> {
                List<SmartNotificationEvent> events = new ArrayList<>();
                if (type == SmartNotificationEventType.METER_DR) {
                    createMeterDrEvents(type, events);
                } else if (type == SmartNotificationEventType.INFRASTRUCTURE_WARNING) {
                    createInfrastructureWaringEvent(type, eventsPerMessage, numberOfMessages, waitTime);
                } else if (type == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
                    createDeviceDataMonitorEvents(subscriptionsByType.get(type), type, eventsPerMessage, numberOfMessages,
                            waitTime);
                } else {
                    log.info("Event:{} not supported", type);
                }
            });
        });
        return new SimulatorResponseBase(true);
    }
    
    
    private void createInfrastructureWaringEvent(SmartNotificationEventType type,
            int eventsPerMessage, int numberOfMessages, int waitTime) {
        List<InfrastructureWarningType> types = Lists.newArrayList(InfrastructureWarningType.values());
        List<SmartNotificationEvent> events = new ArrayList<>();
        int totalMsgs = numberOfMessages * eventsPerMessage;
        for (int i = 0; i < totalMsgs; i++) {
            int index = rand.nextInt(types.size());
            InfrastructureWarning warning = infrastructureWarningsGeneratorService.generate(types.get(index));
            events.add(InfrastructureWarningsEventAssembler.assemble(Instant.now(), warning));
        }
        if (waitTime > 0) {
            send(events, type, eventsPerMessage, waitTime);
        } else {
            eventCreationService.send(type, events);
            log.info("Completed sending events:{} all at once for:{}", events.size(), type);
        }
    }
    private void createDeviceDataMonitorEvents(List<SmartNotificationSubscription> subscriptions, SmartNotificationEventType type,
            int eventsPerMessage, int numberOfMessages, int waitTime) {
        List<SmartNotificationEvent> events = new ArrayList<>();
        
        List<DeviceDataMonitor> monitors;
        if(subscriptions == null || subscriptions.isEmpty()) {
            //if there is no subscriptions to any DDM send messages to all, confirm that no emails were send
            monitors = monitorCacheService.getDeviceDataMonitors();
        } else {
            //get all monitors for subscriptions DDM send messages to all
            monitors = subscriptions.stream().map(sub -> {
                int monitorId = DeviceDataMonitorEventAssembler.getMonitorId(sub.getParameters());
                return monitorCacheService.getDeviceMonitor(monitorId);
            }).distinct().collect(Collectors.toList());
        }
     
        if (monitors.isEmpty()) {
            return;
        }

        monitors.forEach(monitor -> {
            List<Integer> allIds = new ArrayList<Integer>(deviceGroupService.getDeviceIds(List.of(monitor.getGroup())));
            for (int i = 0; i < numberOfMessages * numberOfMessages; i++) {
                int index = rand.nextInt(allIds.size());
                events.add(DeviceDataMonitorEventAssembler.assemble(Instant.now(), monitor.getId(),
                        monitor.getName(), MonitorState.IN_VIOLATION, allIds.get(index)));
            }
        });
        if (waitTime > 0) {
            send(events, type, eventsPerMessage, waitTime);
        } else {
            eventCreationService.send(type, events);
            log.info("Completed sending events:{} all at once for:{}", events.size(), type);
        }
    }
    
    
    private void send(List<SmartNotificationEvent> events, SmartNotificationEventType type, int eventsPerMessage, int waitTime) {
        for (List<SmartNotificationEvent> part : Lists.partition(events, eventsPerMessage)) {
            try {
                part.forEach(p -> p.setTimestamp(Instant.now()));
                log.info("Waited:{}s Events per msg:{} Tolal:{} Events:{}", waitTime, eventsPerMessage, part.size(), part);
                eventCreationService.send(type, part);
                TimeUnit.SECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
            }
        }
        log.info("Complete sending events:{} wait seconds:{} for:{}",  waitTime, events.size());
    }

    private void createMeterDrEvents(SmartNotificationEventType type, List<SmartNotificationEvent> events) {
        Map<String, Long> statistics = new HashMap<>();
        List<String> statuses = Lists.newArrayList(DrMeterControlStatus.FAILED_ARMED.name(),
            DrMeterControlStatus.CONTROL_CONFIRMED.name(), DrMeterControlStatus.CONTROL_FAILED.name(),
            DrMeterControlStatus.CONTROL_FAILED.name(), DrMeterControlStatus.CONTROL_UNKNOWN.name());
        for (int i = 0; i < 10; i++) {
            String randomElement = statuses.get(rand.nextInt(statuses.size()));
            statistics.compute(randomElement, (key, value) -> value == null ? 1 : value + 1);
        }
        events.add(MeterDrEventAssembler.assemble(statistics, "Test Program"));
        eventCreationService.send(type, events);
        log.info("Sending events:{} for:{}", events.size());
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
        jmsTemplate.convertAndSend(new DailyDigestTestParams(dailyDigestHour));
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
    
    @Override
    public SmartNotificationSimulatorSettings getCurrentSettings() {
        log.debug("Getting SmartNotificationSimlatorSettings from db.");
        return new SmartNotificationSimulatorSettings(
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_DAILY_DIGEST_HOUR),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_USER_GROUP_ID),
            yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_GENERATE_TEST_EMAIL),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE),
            yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_WAIT_TIME_SEC));
    }

}
